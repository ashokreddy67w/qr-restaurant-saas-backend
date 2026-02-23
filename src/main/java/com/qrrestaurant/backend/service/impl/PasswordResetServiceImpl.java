package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.AuthResponse;
import com.qrrestaurant.backend.dto.ForgotPasswordRequest;
import com.qrrestaurant.backend.dto.OtpVerificationRequest;
import com.qrrestaurant.backend.dto.ResetPasswordRequest;
import com.qrrestaurant.backend.entity.PasswordResetToken;
import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.repository.PasswordResetTokenRepository;
import com.qrrestaurant.backend.repository.PlatformAdminRepository;
import com.qrrestaurant.backend.service.EmailService;
import com.qrrestaurant.backend.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    
    private final PlatformAdminRepository adminRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    public PasswordResetServiceImpl(
            PlatformAdminRepository adminRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password for email: {}", request.getEmail());
        
        PlatformAdmin admin = adminRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (admin == null) {
            log.warn("Password reset requested for non-existent email: {}", request.getEmail());
            return createErrorResponse("If your email is registered, you will receive an OTP shortly.");
        }
        
        // Delete any existing tokens
        tokenRepository.deleteByAdmin(admin);
        
        // Generate OTP
        String otp = generateOtp();
        log.debug("Generated OTP for {}: {}", request.getEmail(), otp);
        
        // Create new token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(otp);
        resetToken.setAdmin(admin);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);  // ✅ Now works
        
        tokenRepository.save(resetToken);
        log.info("Password reset token saved for admin ID: {}", admin.getId());
        
        // Send email
        emailService.sendPasswordResetOtp(admin.getEmail(), otp, admin.getName());
        
        AuthResponse response = new AuthResponse();
        response.setMessage("OTP sent to your email. Valid for 15 minutes.");
        response.setEmail(admin.getEmail());
        return response;
    }
    
    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());
        
        PlatformAdmin admin = adminRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (admin == null) {
            return createErrorResponse("Invalid request");
        }
        
        PasswordResetToken token = tokenRepository.findByAdminAndUsedFalse(admin)
                .orElse(null);
        
        if (token == null) {
            log.warn("No valid token found for admin ID: {}", admin.getId());
            return createErrorResponse("No valid OTP found. Please request a new one.");
        }
        
        if (token.isExpired()) {
            log.warn("Token expired for admin ID: {}", admin.getId());
            tokenRepository.delete(token);
            return createErrorResponse("OTP has expired. Please request a new one.");
        }
        
        if (!token.getToken().equals(request.getOtp())) {
            log.warn("Invalid OTP for admin ID: {}", admin.getId());
            return createErrorResponse("Invalid OTP");
        }
        
        // Generate new secure token
        String resetToken = UUID.randomUUID().toString();
        token.setToken(resetToken);
        tokenRepository.save(token);
        
        log.info("OTP verified successfully for admin ID: {}", admin.getId());
        
        AuthResponse response = new AuthResponse();
        response.setMessage("OTP verified successfully");
        response.setToken(resetToken);
        return response;
    }
    
    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset request");
        
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password reset failed: Passwords do not match");
            return createErrorResponse("Passwords do not match");
        }
        
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElse(null);
        
        if (resetToken == null) {
            log.warn("Password reset failed: Invalid token");
            return createErrorResponse("Invalid or expired token");
        }
        
        // ✅ Now isUsed() works
        if (resetToken.isUsed()) {
            log.warn("Password reset failed: Token already used");
            return createErrorResponse("Token has already been used");
        }
        
        if (resetToken.isExpired()) {
            log.warn("Password reset failed: Token expired");
            tokenRepository.delete(resetToken);
            return createErrorResponse("Token has expired. Please request a new one.");
        }
        
        PlatformAdmin admin = resetToken.getAdmin();
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
        
        // ✅ Now setUsed() works
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        emailService.sendPasswordResetConfirmation(admin.getEmail(), admin.getName());
        
        log.info("Password reset successful for admin ID: {}", admin.getId());
        
        AuthResponse response = new AuthResponse();
        response.setMessage("Password reset successful. You can now login with your new password.");
        return response;
    }
    
    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    @Override
    public boolean isValidResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);
        
        if (resetToken == null) {
            return false;
        }
        
        return !resetToken.isUsed() && !resetToken.isExpired();  // ✅ Now isUsed() works
    }
    
    // Helper method to create error responses
    private AuthResponse createErrorResponse(String message) {
        AuthResponse response = new AuthResponse();
        response.setMessage(message);
        return response;
    }
}