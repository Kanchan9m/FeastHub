package com.example.project.dto.response;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

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

    private LocalTime openingTime;
    private LocalTime closingTime;
    private Set<DayOfWeek> openDays;

    private String image;
}
