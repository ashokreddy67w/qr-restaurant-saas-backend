package com.qrrestaurant.backend.service;

public interface EmailService {
    
    void sendPasswordResetOtp(String to, String otp, String name);
    
    void sendPasswordResetConfirmation(String to, String name);
    
    void sendWelcomeEmail(String to, String name);
    
    void sendVerificationEmail(String to, String verificationLink, String name);  // ✅ THIS MUST BE HERE
    
    void sendAccountActivationEmail(String to, String name);
    
    void sendLoginAlert(String to, String name, String ipAddress);
}