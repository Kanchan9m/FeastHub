package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RazorpaySubscriptionResponse {

    private String subscriptionId;
    private String keyId;
    private String planName;
    private Integer restaurantLimit;
}
