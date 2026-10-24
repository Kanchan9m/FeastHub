package com.example.project.controller;

import com.example.project.dto.request.RestaurantRequest;
import com.example.project.dto.response.LocationResponse;
import com.example.project.dto.response.RestaurantResponse;
import com.example.project.dto.response.SubscriptionResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.AdminAuthorizationService;
import com.example.project.service.GeocodingService;
import com.example.project.service.RestaurantService;
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
@RequestMapping("/rms")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private AdminAuthorizationService adminAuthorizationService;

    @Autowired
    private GeocodingService geocodingService;

    @PostMapping("/owner/restaurant")
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestPart("restaurant") RestaurantRequest request,
                                                               @RequestPart(value = "image", required = false) MultipartFile image,
                                                               Authentication authentication) {
//        checkAdmin(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RestaurantResponse response = restaurantService.createRestaurant(request, image, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/owner/restaurant")
    public ResponseEntity<List<RestaurantResponse>> getOwnerRestaurants(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(restaurantService.getOwnerRestaurants(userDetails.getId()));
    }

//    @GetMapping("/owner/restaurant")
//    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants(Authentication authentication) {
//        UserDetailsImpl userDetails =
//                (UserDetailsImpl) authentication.getPrincipal();
//
////        return ResponseEntity.ok(
////                restaurantService.getOwnerRestaurants(
////                        userDetails.getId()
////                )
////        );
//
//        return ResponseEntity.ok(restaurantService.getAllRestaurants());
//    }

    @GetMapping("/owner/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long restaurantId,
                                                                Authentication authentication) {

//        checkAdmin(authentication);
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(restaurantService.getRestaurantById(restaurantId));
    }

    @PutMapping(
            value = "/owner/restaurant/{restaurantId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long restaurantId,
            @Valid @RequestPart("restaurant") RestaurantRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) {

        System.out.println("=== UPDATE RESTAURANT REQUEST ===");
        System.out.println("Name: " + request.getRestaurantName());
        System.out.println("Address: " + request.getAddress());
        System.out.println("Phone: " + request.getPhone());
        System.out.println("State: " + request.getState());
        System.out.println("City: " + request.getCity());
        System.out.println("Pincode: " + request.getPincode());

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        RestaurantResponse response =
                restaurantService.updateRestaurant(
                        restaurantId,
                        request,
                        image,
                        userDetails.getId()
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/restaurant/{restaurantId}/approve")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable Long restaurantId,
                                                                Authentication authentication) {

        checkAdmin(authentication);
        return ResponseEntity.ok(restaurantService.approveRestaurant(restaurantId));
    }

    @DeleteMapping("/admin/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId,
                                                 Authentication authentication) {

        checkAdmin(authentication);

        restaurantService.deleteRestaurant(restaurantId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/restaurant/{restaurantId}/owner/{userId}")
    public ResponseEntity<String> assignOwner(@PathVariable Long restaurantId,
                                              @PathVariable Long userId, Authentication authentication) {

        checkAdmin(authentication);

        restaurantService.assignOwner(restaurantId, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body("Owner assigned successfully");
    }

    @GetMapping("/owner/subscription")
    public ResponseEntity<SubscriptionResponse> getOwnerSubscription(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(restaurantService.getOwnerSubscription(userDetails.getId()));
    }

    @GetMapping("/owner/location/reverse")
    public ResponseEntity<?> reverseGeocode(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        if (latitude < -90 || latitude > 90
                || longitude < -180 || longitude > 180) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of(
                            "message", "Invalid latitude or longitude"
                    ));
        }

        try {
            LocationResponse response =
                    geocodingService.reverseGeocode(latitude, longitude);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of(
                            "message", ex.getMessage()
                    ));

        } catch (org.springframework.web.client.RestClientException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of(
                            "message",
                            "Location service is temporarily unavailable"
                    ));
        }
    }


    @GetMapping("/owner/location/lookup")
    public ResponseEntity<?> lookupCity(
            @RequestParam String city) {

        try {
            LocationResponse response = geocodingService.findCity(city);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of(
                            "message", ex.getMessage()
                    ));
        } catch (org.springframework.web.client.RestClientException ex) {
            return ResponseEntity.status(
                    org.springframework.http.HttpStatus.BAD_GATEWAY
            ).body(java.util.Map.of(
                    "message", "Location service is temporarily unavailable"
            ));
        }
    }



    @GetMapping("/owner/location/search")
    public ResponseEntity<?> searchAddress(
            @RequestParam("address") String address) {

        System.out.println("=== LOCATION SEARCH ENDPOINT HIT ===");
        System.out.println("Address received: [" + address + "]");

        try {
            LocationResponse result = geocodingService.findAddress(address.trim());

            System.out.println("Geocoding result: " + result);

            if (result == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(java.util.Map.of(
                                "message",
                                "Address not found by geocoding service"
                        ));
            }

            return ResponseEntity.ok(result);

        } catch (org.springframework.web.client.RestClientException ex) {
            ex.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of(
                            "message", "External geocoding service failed"
                    ));
        }
    }




    private void checkAdmin(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        adminAuthorizationService.checkAdmin(userDetails.getId());
    }



}
