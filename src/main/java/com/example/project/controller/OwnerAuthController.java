package com.example.project.controller;

import com.example.project.dto.request.OwnerRequest;
import com.example.project.dto.response.OwnerRegisterResponse;
import com.example.project.service.OwnerAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rms/owner")
public class OwnerAuthController {

    @Autowired
    private OwnerAuthService ownerAuthService;

    @PostMapping("/register")
    public ResponseEntity<OwnerRegisterResponse> registerOwner(@Valid @RequestBody OwnerRequest request) {

        var owner = ownerAuthService.registerOwner(request);

        OwnerRegisterResponse response = new OwnerRegisterResponse(
                        owner.getId(),
                        owner.getUserName(),
                        owner.getEmail(),
                        owner.getPhone(),
                        owner.getEmailVerified(),
                        owner.getActive(),
                        "Registration successful. Please verify your email.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


        @PostMapping("/verify-email")
        public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String otp) {

            boolean verified = ownerAuthService.verifyEmail(email, otp);

            if (!verified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        java.util.Map.of("message", "Invalid or expired OTP"));
            }

            return ResponseEntity.ok(java.util.Map.of("message",
                    "Email verified successfully. You can now login."));
        }

        @PostMapping("/resend-verification")
        public ResponseEntity<?> resendVerificationOtp(@RequestParam String email) {

            ownerAuthService.resendVerificationOtp(email);
            return ResponseEntity.ok(java.util.Map.of("message", "OTP sent successfully"));
        }
}
