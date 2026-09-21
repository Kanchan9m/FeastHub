package com.example.project.service;

import com.example.project.dto.response.RazorpaySubscriptionResponse;

public interface RazorpaySubscriptionService {

    RazorpaySubscriptionResponse createPremiumSubscription(Long ownerId);

    void verifyPayment(
            Long ownerId,
            String razorpayPaymentId,
            String razorpaySubscriptionId,
            String razorpaySignature
    );
}
