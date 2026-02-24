package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.dto.request.ForgotPasswordRequest;
import com.qrrestaurant.backend.dto.request.OtpVerificationRequest;
import com.qrrestaurant.backend.dto.request.ResetPasswordRequest;
import com.qrrestaurant.backend.service.PasswordResetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordController {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);  // Manual logger
    
    private final PasswordResetService passwordResetService;  // final field
    
    // ✅ Constructor to initialize final field
    public PasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Processing forgot password request for email: {}", request.getEmail());
        
        AuthResponse response = passwordResetService.forgotPassword(request);
        
        if (response.getMessage() != null && response.getMessage().contains("deactivated")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        log.info("Verifying OTP for email: {}", request.getEmail());
        
        AuthResponse response = passwordResetService.verifyOtp(request);
        
        if (response.getMessage() != null && 
            (response.getMessage().contains("Invalid") || 
             response.getMessage().contains("No valid"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        if (response.getMessage() != null && response.getMessage().contains("expired")) {
            return ResponseEntity.status(HttpStatus.GONE).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Processing password reset request");
        
        AuthResponse response = passwordResetService.resetPassword(request);
        
        if (response.getMessage() != null && response.getMessage().contains("do not match")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        if (response.getMessage() != null && 
            (response.getMessage().contains("Invalid") || 
             response.getMessage().contains("expired") ||
             response.getMessage().contains("already used"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        log.info("Validating reset token");
        
        boolean isValid = passwordResetService.isValidResetToken(token);
        
        if (isValid) {
            AuthResponse response = new AuthResponse();
            response.setMessage("Token is valid");
            return ResponseEntity.ok(response);
        } else {
            AuthResponse response = new AuthResponse();
            response.setMessage("Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}