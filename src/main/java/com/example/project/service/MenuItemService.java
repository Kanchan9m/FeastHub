package com.example.project.service;

import com.example.project.model.MenuItem;

import java.util.List;

public interface MenuItemService {

    MenuItem createMenuItem(Long restaurantId, MenuItem menuItem);

    List<MenuItem> getMenuItemByRestaurant(Long restaurantId);

    MenuItem getMenuItemById(Long menuItemId);

    MenuItem updateMenuItem(Long restaurantId, MenuItem menuItem);

    void deleteMenuItem(Long menuItemId);
}
