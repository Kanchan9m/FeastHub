package com.example.project.service;

import com.example.project.dto.request.MenuItemRequest;
import com.example.project.dto.response.MenuItemResponse;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.MenuItem;
import com.example.project.model.Restaurants;
import com.example.project.repositories.MenuItemRepository;
import com.example.project.repositories.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService{

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MenuItemResponse createMenuItem(
            Long restaurantId,
            MenuItemRequest request,
            MultipartFile image) {

        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurant not found"));

        MenuItem menuItem = new MenuItem();

        menuItem.setMenuName(request.getMenuName());
        menuItem.setDescription(request.getDescription());
        menuItem.setCategory(request.getCategory());
        menuItem.setPrice(request.getPrice());

        // Save the uploaded image and store its URL/path in the entity.
        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            imagePath = imageStorageService.saveMenuImage(image);
        } else {
            // Allows a supplied image path if no file was uploaded.
            imagePath = request.getImage();
        }

        menuItem.setImage(imagePath);
        menuItem.setAvailable(
                request.getAvailable() == null || request.getAvailable()
        );
        menuItem.setRestaurant(restaurant);

        MenuItem savedMenu = menuItemRepository.save(menuItem);

        return modelMapper.map(savedMenu, MenuItemResponse.class);
    }

    @Override
    public List<MenuItemResponse> getMenuItemByRestaurant(Long restaurantId) {
        List<MenuItem> menuItems =
                menuItemRepository.findByRestaurantId(restaurantId);

        return menuItems.stream()
                .map(item -> modelMapper.map(item, MenuItemResponse.class))
                .toList();
    }

    @Override
    public MenuItemResponse getMenuItemById(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        return modelMapper.map(menuItem, MenuItemResponse.class);
    }

    @Override
    public MenuItemResponse updateMenuItem(
            Long menuItemId,
            MenuItemRequest request,
            MultipartFile image) {

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        menuItem.setMenuName(request.getMenuName());
        menuItem.setDescription(request.getDescription());
        menuItem.setCategory(request.getCategory());
        menuItem.setPrice(request.getPrice());

        if (request.getAvailable() != null) {
            menuItem.setAvailable(request.getAvailable());
        }

        if (image != null && !image.isEmpty()) {
            String imagePath = imageStorageService.saveMenuImage(image);
            menuItem.setImage(imagePath);
        }

        MenuItem updated = menuItemRepository.save(menuItem);

        return modelMapper.map(updated, MenuItemResponse.class);
    }

//    @Override
//    public MenuItem updateMenuItem(Long menuItemId, MenuItem menuItem) {
//        MenuItem existingItem = menuItemRepository.findById(menuItemId)
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("Menu item not found"));
//
//        existingItem.setMenuName(menuItem.getMenuName());
//        existingItem.setDescription(menuItem.getDescription());
//        existingItem.setCategory(menuItem.getCategory());
//        existingItem.setPrice(menuItem.getPrice());
//        existingItem.setImage(menuItem.getImage());
//        existingItem.setAvailable(menuItem.getAvailable());
//
//        return menuItemRepository.save(existingItem);
//    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem existingItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Menu item not found"));

        menuItemRepository.delete(existingItem);
    }
}
