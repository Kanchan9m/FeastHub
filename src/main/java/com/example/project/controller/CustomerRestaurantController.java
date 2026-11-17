
package com.example.project.controller;

import com.example.project.dto.response.RestaurantResponse;
import com.example.project.dto.response.MenuItemResponse;
import com.example.project.service.RestaurantService;
import com.example.project.service.MenuItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rms/customer/restaurants")
public class CustomerRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemService menuItemService;

    // Get all approved restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getRestaurants() {

        return ResponseEntity.ok(
                restaurantService.getCustomerRestaurants()
        );
    }

    // Get details of one approved restaurant
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurant(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                restaurantService.getCustomerRestaurantById(restaurantId)
        );
    }

    // Get available menu items of an approved restaurant
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(
            @PathVariable Long restaurantId) {

        return ResponseEntity.ok(
                menuItemService.getAvailableMenuItems(restaurantId)
        );
    }
}
