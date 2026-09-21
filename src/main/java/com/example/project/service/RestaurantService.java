package com.example.project.service;

import com.example.project.dto.request.RestaurantRequest;
import com.example.project.dto.response.RestaurantResponse;
import com.example.project.dto.response.SubscriptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RestaurantService {
    RestaurantResponse createRestaurant(RestaurantRequest request, Long ownerId);

    RestaurantResponse updateRestaurant(Long restaurantId, RestaurantRequest request);

    RestaurantResponse getRestaurantById(Long restaurantId);

//    List<RestaurantResponse> getAllRestaurants();

    RestaurantResponse approveRestaurant(Long restaurantId);

    void deleteRestaurant(Long restaurantId);

    void assignOwner(Long restaurantId, Long userId);

    List<RestaurantResponse> getOwnerRestaurants(Long id);

    SubscriptionResponse getOwnerSubscription(Long id);

//    RestaurantResponse getOwnerRestaurantById(Long restaurantId, Long id);
}
