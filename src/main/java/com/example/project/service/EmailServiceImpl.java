package com.example.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Override
    public void sendAdminOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);

        message.setSubject("Restaurant Management System - Admin Login Verification");

        message.setText(
                    """
                    Hello Admin,
    
                    Your Admin login verification OTP is:
    
                    %s
    
                    This OTP is valid for 5 minutes.
    
                    If you did not attempt to log in,
                    please ignore this email.
    
                    Regards,
                    Restaurant Management System
                    """.formatted(otp));

        mailSender.send(message);
    }

    @Override
    public void sendOwnerVerificationEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);

        message.setSubject("Restaurant Management System - Email Verification");

        message.setText(
                    """
                    Hello,
    
                    Welcome to the Restaurant Management System.
    
                    Your email verification OTP is:
    
                    %s
    
                    This OTP is valid for 5 minutes.
    
                    Please do not share this OTP with anyone.
    
                    Regards,
                    Restaurant Management System
                    """.formatted(otp)
        );
        mailSender.send(message);
    }
}
