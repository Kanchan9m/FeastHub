package com.example.project.controller;

import com.example.project.dto.request.MenuItemRequest;
import com.example.project.dto.response.MenuItemResponse;
import com.example.project.model.MenuItem;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rms/owner/restaurant")
public class MenuController {

    @Autowired
    private MenuItemService menuItemService;

//    @PostMapping("/{restaurantId}/menu")
//    public ResponseEntity<MenuItemResponse> createMenuItem(@PathVariable Long restaurantId,
//            @Valid @RequestBody MenuItemRequest request) {
//
//        MenuItemResponse response = menuItemService.createMenuItem(restaurantId, request);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @PostMapping(
            value = "/{restaurantId}/menu"
    )
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestPart("menu") MenuItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        MenuItemResponse response =
                menuItemService.createMenuItem(restaurantId, request, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @GetMapping("/menu")
//    public ResponseEntity<List<MenuItem>> getRestaurantMenu(Authentication authentication) {
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//        return ResponseEntity.ok(menuItemService.getMenuItemByRestaurant(userDetails.getId()));
//    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getMenuItems(
            @PathVariable Long restaurantId) {

        List<MenuItemResponse> items =
                menuItemService.getMenuItemByRestaurant(restaurantId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/menu/{menuItemId}")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable Long menuItemId) {

        return ResponseEntity.ok(menuItemService.getMenuItemById(menuItemId));
    }

    @PutMapping(
            value = "/menu/{menuItemId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long menuItemId,
            @Valid @RequestPart("menu") MenuItemRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        MenuItemResponse response =
                menuItemService.updateMenuItem(menuItemId, request, image);

        return ResponseEntity.ok(response);
    }

//    @PutMapping("/menu/{menuItemId}")
//    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long menuItemId,
//            @RequestBody MenuItem menuItem) {
//
//        return ResponseEntity.ok(menuItemService.updateMenuItem(menuItemId, menuItem));
//    }

    @DeleteMapping("/menu/{menuItemId}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable Long menuItemId
    ) {

        menuItemService.deleteMenuItem(menuItemId);

        return ResponseEntity.ok("Menu item deleted successfully");
    }

}
