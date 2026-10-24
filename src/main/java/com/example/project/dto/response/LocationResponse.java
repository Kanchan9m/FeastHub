package com.example.project.dto.response;

import lombok.Data;

@Data
public class LocationResponse {
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private Double latitude;
    private Double longitude;
}
