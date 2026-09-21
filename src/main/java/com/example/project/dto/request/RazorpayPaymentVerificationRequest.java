package com.example.project.dto.request;

import lombok.Data;

@Data
public class RazorpayPaymentVerificationRequest {

    private String razorpayPaymentId;

    private String razorpaySubscriptionId;

    private String razorpaySignature;
}
