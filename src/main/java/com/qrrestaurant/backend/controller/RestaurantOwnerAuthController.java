package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.RestaurantOwnerAuthResponse;
import com.qrrestaurant.backend.dto.request.RestaurantOwnerLoginRequest;
import com.qrrestaurant.backend.entity.RestaurantOwner;
import com.qrrestaurant.backend.repository.RestaurantOwnerRepository;
import com.qrrestaurant.backend.service.RestaurantOwnerJwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/owner")
public class RestaurantOwnerAuthController {
    
    private static final Logger log = LoggerFactory.getLogger(RestaurantOwnerAuthController.class);
    
    private final RestaurantOwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantOwnerJwtService ownerJwtService;
    
    // Manual constructor
    public RestaurantOwnerAuthController(
            RestaurantOwnerRepository ownerRepository,
            PasswordEncoder passwordEncoder,
            RestaurantOwnerJwtService ownerJwtService) {
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
        this.ownerJwtService = ownerJwtService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RestaurantOwnerLoginRequest request) {
        log.info("Owner login attempt: {}", request.getEmail());
        
        RestaurantOwner owner = ownerRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        if (owner == null) {
            RestaurantOwnerAuthResponse response = new RestaurantOwnerAuthResponse();
            response.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        if (!owner.isActive()) {
            RestaurantOwnerAuthResponse response = new RestaurantOwnerAuthResponse();
            response.setMessage("Account is deactivated");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        if (!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
            RestaurantOwnerAuthResponse response = new RestaurantOwnerAuthResponse();
            response.setMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        owner.setLastLoginAt(LocalDateTime.now());
        ownerRepository.save(owner);
        
        String token = ownerJwtService.generateToken(
            owner.getEmail(),
            owner.getId(),
            owner.getName(),
            owner.getRestaurant().getId()
        );
        
        RestaurantOwnerAuthResponse response = new RestaurantOwnerAuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setId(owner.getId());
        response.setEmail(owner.getEmail());
        response.setName(owner.getName());
        response.setRole("OWNER");
        response.setRestaurantId(owner.getRestaurant().getId());
        response.setMessage("Login successful");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        String email = ownerJwtService.extractEmail(token.substring(7));
        RestaurantOwner owner = ownerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        return ResponseEntity.ok(owner);
    }
}