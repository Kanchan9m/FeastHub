package com.example.project.service;

import com.example.project.dto.request.OwnerRequest;
import com.example.project.exception.APIException;
import com.example.project.model.*;
import com.example.project.repositories.OwnerSubscriptionRepository;
import com.example.project.repositories.RoleRepository;
import com.example.project.repositories.SubscriptionPlanRepository;
import com.example.project.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OwnerAuthServiceImpl implements OwnerAuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private OwnerSubscriptionRepository ownerSubscriptionRepository;

    @Override
    @Transactional
    public User registerOwner(OwnerRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Email already exists");
        }

        if (userRepository.existsByUserName(request.getUsername())) {
            throw new APIException("Username already exists");
        }

        User owner = new User();
        owner.setUserName(request.getUsername());
        owner.setEmail(request.getEmail());
        owner.setPhone(request.getPhone());
        owner.setPassword(passwordEncoder.encode(request.getPassword()));

        owner.setIsAdmin(false);
        owner.setEmailVerified(false);
        owner.setActive(false);

        Role ownerRole = roleRepository.findByRoleName(RoleType.ROLE_OWNER)
                .orElseThrow(() -> new APIException("ROLE_OWNER not found"));

        owner.setRole(ownerRole);

        User savedOwner = userRepository.save(owner);

        SubscriptionPlan freePlan = subscriptionPlanRepository.findByPlanType(PlanType.FREE)
                .orElseThrow(() -> new RuntimeException("FREE subscription plan not found"));

        OwnerSubscription subscription = new OwnerSubscription();

        subscription.setOwner(savedOwner);
        subscription.setPlan(freePlan);
        subscription.setActive(true);
        subscription.setStartDate(LocalDateTime.now());

        ownerSubscriptionRepository.save(subscription);

        emailVerificationService.generateAndSendOtp(savedOwner);
        return savedOwner;
    }

    @Override
    @Transactional
    public boolean verifyEmail(String email, String otp) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new APIException("User not found"));

        if (Boolean.TRUE.equals(owner.getEmailVerified())) {
            return true;
        }

        /*
         * EmailVerificationService handles:
         *
         * - OTP existence
         * - OTP expiry
         * - OTP attempts
         * - OTP hash comparison
         * - used flag
         * - emailVerified=true
         * - active=true
         */
        return emailVerificationService.verifyOtp(owner, otp);
    }


    @Override
    @Transactional
    public void resendVerificationOtp(String email) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new APIException("User not found"));

        if (Boolean.TRUE.equals(owner.getEmailVerified())) {
            throw new APIException("Email is already verified");
        }

        emailVerificationService.generateAndSendOtp(owner);
    }

}
