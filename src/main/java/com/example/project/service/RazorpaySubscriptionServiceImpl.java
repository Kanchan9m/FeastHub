package com.example.project.service;

import com.example.project.dto.response.RazorpaySubscriptionResponse;
import com.example.project.exception.APIException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.OwnerSubscription;
import com.example.project.model.PlanType;
import com.example.project.model.SubscriptionPlan;
import com.example.project.model.User;
import com.example.project.repositories.OwnerSubscriptionRepository;
import com.example.project.repositories.SubscriptionPlanRepository;
import com.example.project.repositories.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Subscription;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class RazorpaySubscriptionServiceImpl implements RazorpaySubscriptionService{

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private OwnerSubscriptionRepository ownerSubscriptionRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.premium.plan.id}")
    private String premiumPlanId;

    @Override
    @Transactional
    public RazorpaySubscriptionResponse createPremiumSubscription(Long ownerId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found with id: " + ownerId)
                );

        OwnerSubscription ownerSubscription = ownerSubscriptionRepository.findByOwnerAndActiveTrue(owner)
                        .orElseThrow(() -> new APIException("No active subscription found"));

        if (ownerSubscription.getPlan().getPlanType() == PlanType.PREMIUM) {
            throw new APIException("Owner already has a Premium subscription");
        }

        SubscriptionPlan premiumPlan = subscriptionPlanRepository.findByPlanType(PlanType.PREMIUM)
                        .orElseThrow(() -> new ResourceNotFoundException("Premium plan not found"));

        try {

            JSONObject subscriptionRequest = new JSONObject();

            subscriptionRequest.put("plan_id", premiumPlanId);

            /*
             * Razorpay requires total_count or end_at.
             *
             * 1200 monthly cycles = 100 years.
             * This gives us practically long-running monthly billing
             * while satisfying Razorpay's bounded subscription requirement.
             */
            subscriptionRequest.put("total_count", 1200);

            subscriptionRequest.put("quantity", 1);

            subscriptionRequest.put(
                    "customer_notify",
                    true
            );

            Subscription razorpaySubscription = razorpayClient.subscriptions.create(subscriptionRequest);

            String subscriptionId = razorpaySubscription.get("id");

            String status = razorpaySubscription.get("status");

            /*
             * Store the Razorpay subscription ID now.
             * The actual Premium upgrade happens only after
             * successful payment verification.
             */
            ownerSubscription.setRazorpaySubscriptionId(
                    subscriptionId
            );

            ownerSubscription.setRazorpayStatus(
                    status
            );

            ownerSubscriptionRepository.save(ownerSubscription);

            return new RazorpaySubscriptionResponse(
                    subscriptionId,
                    razorpayKeyId,
                    premiumPlan.getPlanName(),
                    premiumPlan.getRestaurantLimit()
            );

        } catch (RazorpayException e) {

            throw new APIException("Unable to create Razorpay subscription: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyPayment(
            Long ownerId,
            String razorpayPaymentId,
            String razorpaySubscriptionId,
            String razorpaySignature
    ) {

        if (razorpayPaymentId == null ||
                razorpaySubscriptionId == null ||
                razorpaySignature == null) {

            throw new APIException(
                    "Missing Razorpay payment details"
            );
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: " + ownerId
                        )
                );

        OwnerSubscription ownerSubscription =
                ownerSubscriptionRepository
                        .findByOwnerAndActiveTrue(owner)
                        .orElseThrow(() ->
                                new APIException(
                                        "No active subscription found"
                                )
                        );

        if (!razorpaySubscriptionId.equals(
                ownerSubscription.getRazorpaySubscriptionId()
        )) {

            throw new APIException(
                    "Razorpay subscription does not belong to this owner"
            );
        }

        try {

            String payload =
                    razorpayPaymentId +
                            "|" +
                            razorpaySubscriptionId;

            String generatedSignature =
                    generateHmacSha256(
                            payload,
                            razorpayKeySecret
                    );

            if (!generatedSignature.equals(
                    razorpaySignature
            )) {

                throw new APIException(
                        "Invalid Razorpay payment signature"
                );
            }

            /*
             * Signature is valid.
             * Upgrade the owner to Premium.
             */
            SubscriptionPlan premiumPlan =
                    subscriptionPlanRepository
                            .findByPlanType(PlanType.PREMIUM)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Premium plan not found"
                                    )
                            );

            ownerSubscription.setPlan(
                    premiumPlan
            );

            ownerSubscription.setActive(true);

            ownerSubscription.setStartDate(
                    java.time.LocalDateTime.now()
            );

            ownerSubscription.setRazorpayStatus(
                    "active"
            );

            ownerSubscriptionRepository.save(
                    ownerSubscription
            );

        } catch (Exception e) {

            if (e instanceof APIException) {
                throw (APIException) e;
            }

            throw new APIException(
                    "Payment verification failed"
            );
        }
    }

    private String generateHmacSha256(
            String data,
            String secret
    ) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );

        mac.init(secretKeySpec);

        byte[] hash =
                mac.doFinal(
                        data.getBytes(StandardCharsets.UTF_8)
                );

        StringBuilder hexString =
                new StringBuilder();

        for (byte b : hash) {
            String hex =
                    Integer.toHexString(
                            0xff & b
                    );

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}
