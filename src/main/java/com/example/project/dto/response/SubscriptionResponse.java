package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionResponse {

    private String plan;
    private String planName;
    private Integer restaurantLimit;
    private long restaurantCount;
    private long remainingRestaurants;
}
