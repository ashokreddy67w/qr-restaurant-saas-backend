package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.dto.request.ForgotPasswordRequest;
import com.qrrestaurant.backend.dto.request.OtpVerificationRequest;
import com.qrrestaurant.backend.dto.request.ResetPasswordRequest;

public interface PasswordResetService {
    
    /**
     * Initiate password reset process - sends OTP to email
     */
    AuthResponse forgotPassword(ForgotPasswordRequest request);
    
    /**
     * Verify OTP and generate reset token
     */
    AuthResponse verifyOtp(OtpVerificationRequest request);
    
    /**
     * Reset password using valid token
     */
    AuthResponse resetPassword(ResetPasswordRequest request);
    
    /**
     * Generate 6-digit OTP
     */
    String generateOtp();
    
    /**
     * Validate if reset token is valid
     */
    boolean isValidResetToken(String token);
}