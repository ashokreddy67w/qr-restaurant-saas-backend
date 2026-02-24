package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.dto.request.LoginRequest;
import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.service.AuthService;
import com.qrrestaurant.backend.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    
    // Constructor injection
    public AuthController(AuthService authService, EmailVerificationService emailVerificationService) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PlatformAdmin admin) {
        log.info("Register request for email: {}", admin.getEmail());
        
        AuthResponse response = authService.register(admin);
        
        if (response.getMessage() != null && response.getMessage().contains("already")) {
            log.warn("Registration failed - email already exists: {}", admin.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        log.info("Registration successful for: {}", admin.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Email verification request with token: {}", token);
        
        AuthResponse response = emailVerificationService.verifyEmail(token);
        
        if (response.getMessage().contains("Invalid") || 
            response.getMessage().contains("expired") ||
            response.getMessage().contains("already used")) {
            log.warn("Email verification failed: {}", response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        log.info("Email verified successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam String email) {
        log.info("Resend verification email request for: {}", email);
        
        // You'll need to implement this in your service
        // This is a placeholder - you need to add this method to EmailVerificationService
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Verification email resent. Please check your inbox.")
                .build());
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        if (response.getMessage() != null && !response.getMessage().contains("successful")) {
            log.warn("Login failed for {}: {}", request.getEmail(), response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        log.info("Login successful for: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token validation request");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid token format");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Invalid token format").build());
        }
        
        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);
        
        if (isValid) {
            log.info("Token is valid");
            return ResponseEntity.ok(AuthResponse.builder()
                    .message("Token is valid")
                    .build());
        } else {
            log.warn("Token expired or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Token expired or invalid").build());
        }
    }
}