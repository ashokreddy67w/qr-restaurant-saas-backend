package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.dto.AuthResponse;
import com.qrrestaurant.backend.entity.PlatformAdmin;

public interface EmailVerificationService {
    
    String createVerificationToken(PlatformAdmin admin);
    
    AuthResponse verifyEmail(String token);
    
    void sendVerificationEmail(PlatformAdmin admin);
    
    boolean isEmailVerified(String email);
    AuthResponse resendVerificationEmail(String email);
}