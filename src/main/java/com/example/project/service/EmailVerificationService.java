package com.example.project.service;

import com.example.project.model.User;
import jakarta.transaction.Transactional;

public interface EmailVerificationService {
    void generateAndSendOtp(User user);

    boolean verifyOtp(User user, String otp);

    void invalidatePreviousOtp(User user);

}
