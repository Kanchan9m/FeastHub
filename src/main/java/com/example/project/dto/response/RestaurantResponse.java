package com.example.project.dto.response;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private Long id;

    private String restaurantName;

    private String address;

    private String phone;

    private String state;

    private String city;

    private String pincode;

    private Boolean approved;
}
