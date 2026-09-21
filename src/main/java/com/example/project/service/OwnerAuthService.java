package com.example.project.service;

import com.example.project.dto.request.OwnerRequest;
import com.example.project.model.User;

public interface OwnerAuthService {

    User registerOwner(OwnerRequest request);

    boolean verifyEmail(String email, String otp);

    void resendVerificationOtp(String email);
}
