package com.example.project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Data
@Getter
@Setter
public class RestaurantRequest {

    @NotBlank
    private String restaurantName;

    @NotBlank
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank
    private String state;

    @NotBlank
    private String city;

    @NotBlank
    private String pincode;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private Set<DayOfWeek> openDays;

}
