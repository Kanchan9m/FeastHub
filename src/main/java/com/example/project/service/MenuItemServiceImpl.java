package com.example.project.service;

import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.MenuItem;
import com.example.project.model.Restaurants;
import com.example.project.repositories.MenuItemRepository;
import com.example.project.repositories.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService{

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public MenuItem createMenuItem(Long restaurantId, MenuItem menuItem) {
        Restaurants restaurants = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResolutionException("Restaurant not Found"));

        menuItem.setRestaurant(restaurants);
        return menuItemRepository.save(menuItem);
    }

    @Override
    public List<MenuItem> getMenuItemByRestaurant(Long restaurantId) {

        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found"));
    }

    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItem menuItem) {
        MenuItem existItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found"));

        existItem.setMenuName(menuItem.getMenuName());
        existItem.setDescription(menuItem.getDescription());
        existItem.setCategory(menuItem.getCategory());
        existItem.setPrice(menuItem.getPrice());
        existItem.setImage(menuItem.getImage());
        existItem.setAvailable(menuItem.getAvailable());

        return menuItemRepository.save(existItem);
    }

    @Override
    public void deleteMenuItem(Long menuItemId) {
        MenuItem existItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found"));

        menuItemRepository.delete(existItem);
    }
}
