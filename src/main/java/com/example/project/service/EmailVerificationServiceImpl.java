package com.example.project.service;

import com.example.project.model.EmailVerificationOtp;
import com.example.project.model.User;
import com.example.project.repositories.EmailVerificationOtpRepository;
import com.example.project.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long OTP_EXPIRY_MINUTES = 5;

    @Autowired
    private EmailVerificationOtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecureRandom secureRandom;

    @Override
    @Transactional
    public void generateAndSendOtp(User user) {

        otpRepository.invalidatePreviousOtps(user);

        String otp = generateOtp();

        String otpHash = passwordEncoder.encode(otp);

        EmailVerificationOtp verificationOtp = new EmailVerificationOtp();

        verificationOtp.setUser(user);

        verificationOtp.setOtpHash(otpHash);

        verificationOtp.setCreatedAt(Instant.now());

        verificationOtp.setExpiresAt(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));

        verificationOtp.setUsed(false);

        verificationOtp.setAttemptCount(0);

        otpRepository.save(verificationOtp);

        emailService.sendOwnerVerificationEmail(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public boolean verifyOtp(User user, String otp) {

        EmailVerificationOtp verificationOtp = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElse(null);

        if (verificationOtp == null) {
            return false;
        }
        if (Boolean.TRUE.equals(verificationOtp.getUsed())) {
            return false;
        }

        if (verificationOtp.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        if (verificationOtp.getAttemptCount() >= MAX_ATTEMPTS) {
            return false;
        }
        boolean valid = passwordEncoder.matches(otp, verificationOtp.getOtpHash());
        if (!valid) {
            verificationOtp.setAttemptCount(verificationOtp.getAttemptCount() + 1);
            otpRepository.save(verificationOtp);
            return false;
        }

        verificationOtp.setUsed(true);
        otpRepository.save(verificationOtp);

        user.setEmailVerified(true);

        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public void invalidatePreviousOtp(User user) {
        otpRepository.invalidatePreviousOtps(user);

    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format(
                "%06d", otp);
    }
}
