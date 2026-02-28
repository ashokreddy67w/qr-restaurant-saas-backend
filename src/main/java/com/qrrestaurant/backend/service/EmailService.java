package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.entity.Restaurant;

public interface EmailService {
    
    void sendPasswordResetOtp(String to, String otp, String name);
    
    void sendPasswordResetConfirmation(String to, String name);
    
    void sendWelcomeEmail(String to, String name);
    
    void sendVerificationEmail(String to, String verificationLink, String name);  // ✅ THIS MUST BE HERE
    
    void sendAccountActivationEmail(String to, String name);
    
    void sendLoginAlert(String to, String name, String ipAddress);
    void sendRestaurantRegistrationConfirmation(String to, String restaurantName);
    
    void sendApprovalEmail(Restaurant restaurant, String rawPassword);
    
    void sendRejectionEmail(Restaurant restaurant, String reason);
    // ADD THESE
    void sendSuspensionEmail(Restaurant restaurant, String reason);
    void sendReopenEmail(Restaurant restaurant);
    
    
    
   
}