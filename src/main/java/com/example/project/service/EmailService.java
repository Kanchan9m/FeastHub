package com.example.project.service;

public interface EmailService {
    void sendAdminOtpEmail(String email, String otp);

    void sendOwnerVerificationEmail(String email, String otp);

    void sendEmployeeInvitationEmail(String email, String employeeName,
                                     String restaurantName, String invitationLink);
}
