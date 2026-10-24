package com.example.project.controller;

import com.example.project.dto.request.RazorpayPaymentVerificationRequest;
import com.example.project.dto.response.RazorpaySubscriptionResponse;
import com.example.project.dto.response.SubscriptionResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.RazorpaySubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rms/owner/subscription")
public class RazorpaySubscriptionController {

    @Autowired
    private RazorpaySubscriptionService razorpaySubscriptionService;


    @PostMapping("/create")
    public ResponseEntity<RazorpaySubscriptionResponse> createPremiumSubscription(Authentication authentication
    ) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        RazorpaySubscriptionResponse response = razorpaySubscriptionService.createPremiumSubscription(userDetails.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody RazorpayPaymentVerificationRequest request,
            Authentication authentication
    ) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        razorpaySubscriptionService.verifyPayment(userDetails.getId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySubscriptionId(),
                request.getRazorpaySignature()
        );

        return ResponseEntity.ok(Map.of("message",
                        "Payment verified successfully. Premium subscription activated."));
    }


//    @GetMapping
//    public ResponseEntity<SubscriptionResponse> getOwnerSubscription(Authentication authentication) {
//
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        SubscriptionResponse response = razorpaySubscriptionService.getOwnerSubscription(userDetails.getId());
//
//        return ResponseEntity.ok(response);
//    }
}
