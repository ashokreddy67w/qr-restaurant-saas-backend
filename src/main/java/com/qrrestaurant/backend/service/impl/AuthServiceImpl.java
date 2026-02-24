package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.repository.PlatformAdminRepository;
import com.qrrestaurant.backend.service.AuthService;
import com.qrrestaurant.backend.service.EmailService;
import com.qrrestaurant.backend.service.EmailVerificationService;  // ← ADD THIS IMPORT
import com.qrrestaurant.backend.service.JwtService;
import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.dto.request.LoginRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final PlatformAdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;  // ← ADD THIS
    
    // UPDATE CONSTRUCTOR
    public AuthServiceImpl(
            PlatformAdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService,
            EmailVerificationService emailVerificationService) {  // ← ADD THIS PARAMETER
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;  // ← ADD THIS
    }
    
    @Override
    public AuthResponse register(PlatformAdmin admin) {
        log.info("Registering new admin with email: {}", admin.getEmail());
        
        if (adminRepository.existsByEmail(admin.getEmail())) {
            log.warn("Registration failed - email already exists: {}", admin.getEmail());
            return createErrorResponse("Email already registered");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setEmailVerified(false);  // IMPORTANT: Not verified yet!
        
        PlatformAdmin savedAdmin = adminRepository.save(admin);
        log.info("Admin saved with ID: {}", savedAdmin.getId());
        
        // ✅ SEND VERIFICATION EMAIL (NOT welcome email yet)
        log.info("Sending verification email to: {}", savedAdmin.getEmail());
        emailVerificationService.sendVerificationEmail(savedAdmin);
        
        // ❌ REMOVE THIS - Welcome email is sent AFTER verification
        // emailService.sendWelcomeEmail(savedAdmin.getEmail(), savedAdmin.getName());
        
        // ✅ DO NOT generate token yet - email not verified!
        return AuthResponse.builder()
                .id(savedAdmin.getId())
                .email(savedAdmin.getEmail())
                .name(savedAdmin.getName())
                .role(savedAdmin.getRole())
                .emailVerified(false)
                .message("Registration successful! Please check your email to verify your account.")
                .build();
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        PlatformAdmin admin = adminRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (admin == null) {
            log.warn("Login failed - email not found: {}", request.getEmail());
            return createErrorResponse("Invalid email or password");
        }
        
        if (!admin.isActive()) {
            log.warn("Login failed - account deactivated: {}", request.getEmail());
            return createErrorResponse("Account is deactivated");
        }
        
        // ✅ CHECK IF EMAIL IS VERIFIED
        if (!admin.isEmailVerified()) {
            log.warn("Login failed - email not verified: {}", request.getEmail());
            AuthResponse response = new AuthResponse();
            response.setMessage("Please verify your email before logging in. Check your inbox.");
            response.setEmail(admin.getEmail());
            response.setEmailVerified(false);
            return response;
        }
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            log.warn("Login failed - invalid password for: {}", request.getEmail());
            return createErrorResponse("Invalid email or password");
        }
        
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        log.info("Login successful for: {}", request.getEmail());
        
        String token = jwtService.generateToken(
            admin.getEmail(),
            admin.getId(),
            admin.getName()
        );
        
        return createSuccessResponse(token, admin, "Login successful");
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            String email = jwtService.extractEmail(token);
            boolean isValid = email != null && !jwtService.isTokenExpired(token);
            log.debug("Token validation for {}: {}", email, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }
    
    // Helper method to create success response
    private AuthResponse createSuccessResponse(String token, PlatformAdmin admin, String message) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setId(admin.getId());
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());
        response.setRole(admin.getRole());
        response.setExpiresIn(jwtService.getExpirationTime());
        response.setMessage(message);
        response.setEmailVerified(admin.isEmailVerified());
        return response;
    }
    
    // Helper method to create error response
    private AuthResponse createErrorResponse(String message) {
        AuthResponse response = new AuthResponse();
        response.setMessage(message);
        return response;
    }
}