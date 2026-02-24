package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.entity.EmailVerificationToken;
import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.repository.EmailVerificationTokenRepository;
import com.qrrestaurant.backend.repository.PlatformAdminRepository;
import com.qrrestaurant.backend.service.EmailService;
import com.qrrestaurant.backend.service.EmailVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);
    
    private final EmailVerificationTokenRepository tokenRepository;
    private final PlatformAdminRepository adminRepository;
    private final EmailService emailService;
    
    // ✅ EXPLICIT CONSTRUCTOR for final fields
    public EmailVerificationServiceImpl(
            EmailVerificationTokenRepository tokenRepository,
            PlatformAdminRepository adminRepository,
            EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.adminRepository = adminRepository;
        this.emailService = emailService;
    }
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Override
    public String createVerificationToken(PlatformAdmin admin) {
        log.info("Creating verification token for admin ID: {}", admin.getId());
        
        // Delete any existing tokens
        tokenRepository.deleteByAdmin(admin);
        
        // Create new token
        EmailVerificationToken verificationToken = new EmailVerificationToken(admin);
        tokenRepository.save(verificationToken);
        
        log.info("✅ Verification token created for admin: {}", admin.getEmail());
        return verificationToken.getToken();
    }
    
    @Override
    @Transactional
    public AuthResponse verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);
        
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElse(null);
        
        if (verificationToken == null) {
            log.warn("❌ Invalid verification token: {}", token);
            return AuthResponse.builder()
                    .message("Invalid verification token")
                    .build();
        }
        
        if (verificationToken.isUsed()) {
            log.warn("❌ Token already used: {}", token);
            return AuthResponse.builder()
                    .message("Token has already been used")
                    .build();
        }
        
        if (verificationToken.isExpired()) {
            log.warn("❌ Token expired: {}", token);
            tokenRepository.delete(verificationToken);
            return AuthResponse.builder()
                    .message("Verification link has expired. Please register again.")
                    .build();
        }
        
        PlatformAdmin admin = verificationToken.getAdmin();
        admin.setEmailVerified(true);
        adminRepository.save(admin);
        
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);
        
        log.info("✅ Email verified successfully for admin: {}", admin.getEmail());
        
        // Send welcome email after verification
        emailService.sendWelcomeEmail(admin.getEmail(), admin.getName());
        
        return AuthResponse.builder()
                .message("Email verified successfully! Welcome to QR Restaurant Platform.")
                .email(admin.getEmail())
                .build();
    }
    
    @Override
    public void sendVerificationEmail(PlatformAdmin admin) {
        log.info("Sending verification email to: {}", admin.getEmail());
        
        String token = createVerificationToken(admin);
        String verificationLink = frontendUrl + "/verify-email?token=" + token;
        
        emailService.sendVerificationEmail(admin.getEmail(), verificationLink, admin.getName());
        
        log.info("✅ Verification email sent to: {}", admin.getEmail());
    }
    
    @Override
    public boolean isEmailVerified(String email) {
        return adminRepository.findByEmail(email)
                .map(PlatformAdmin::isEmailVerified)
                .orElse(false);
    }
    
    @Override
    public AuthResponse resendVerificationEmail(String email) {
        log.info("Resending verification email to: {}", email);
        
        PlatformAdmin admin = adminRepository.findByEmail(email)
                .orElse(null);
        
        if (admin == null) {
            log.warn("Resend failed - email not found: {}", email);
            return AuthResponse.builder()
                    .message("Email not found")
                    .build();
        }
        
        if (admin.isEmailVerified()) {
            log.warn("Resend failed - email already verified: {}", email);
            return AuthResponse.builder()
                    .message("Email already verified")
                    .build();
        }
        
        sendVerificationEmail(admin);
        
        return AuthResponse.builder()
                .message("Verification email resent. Please check your inbox.")
                .email(email)
                .build();
    }
}