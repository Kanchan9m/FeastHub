package com.example.project.service;

import com.example.project.dto.request.MenuItemRequest;
import com.example.project.dto.response.MenuItemResponse;
import com.example.project.model.MenuItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse createMenuItem(Long restaurantId, MenuItemRequest request, MultipartFile image);

    List<MenuItemResponse> getMenuItemByRestaurant(Long restaurantId);

    MenuItemResponse getMenuItemById(Long menuItemId);

    MenuItemResponse updateMenuItem(Long restaurantId, MenuItemRequest menuItem, MultipartFile image);

    void deleteMenuItem(Long menuItemId);
}
