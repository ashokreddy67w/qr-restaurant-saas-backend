package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.Response.RestaurantOwnerAuthResponse;
import com.qrrestaurant.backend.dto.request.RestaurantOwnerLoginRequest;
import com.qrrestaurant.backend.entity.RestaurantOwner;
import com.qrrestaurant.backend.repository.RestaurantOwnerRepository;
import com.qrrestaurant.backend.service.RestaurantOwnerJwtService;
import com.qrrestaurant.backend.service.RestaurantOwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class RestaurantOwnerServiceImpl implements RestaurantOwnerService {
    
    private static final Logger log = LoggerFactory.getLogger(RestaurantOwnerServiceImpl.class);
    
    private final RestaurantOwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantOwnerJwtService jwtService;
    
    // ✅ Single constructor (remove the duplicate)
    public RestaurantOwnerServiceImpl(
            RestaurantOwnerRepository ownerRepository,
            PasswordEncoder passwordEncoder,
            RestaurantOwnerJwtService jwtService) {
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    @Override
    public RestaurantOwnerAuthResponse login(RestaurantOwnerLoginRequest request) {
        log.info("Owner login attempt: {}", request.getEmail());
        
        RestaurantOwner owner = ownerRepository.findByEmail(request.getEmail())
                .orElse(null);
        
        // Create response object
        RestaurantOwnerAuthResponse response = new RestaurantOwnerAuthResponse();
        
        if (owner == null) {
            response.setMessage("Invalid email or password");
            return response;
        }
        
        if (!owner.isActive()) {
            response.setMessage("Account is deactivated");
            return response;
        }
        
        if (!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
            response.setMessage("Invalid email or password");
            return response;
        }
        
        owner.setLastLoginAt(LocalDateTime.now());
        ownerRepository.save(owner);
        
        // ✅ Check method name - use generateOwnerToken or generateToken based on your JwtService
        String token = jwtService.generateToken(  // or generateToken() - check which one exists
            owner.getEmail(),
            owner.getId(),
            owner.getName(),
            owner.getRestaurant().getId()
        );
        
        // ✅ Set values using setters (not builder)
        response.setToken(token);
        response.setType("Bearer");
        response.setId(owner.getId());
        response.setEmail(owner.getEmail());
        response.setName(owner.getName());
        response.setRole("OWNER");
        response.setRestaurantId(owner.getRestaurant().getId());
        response.setMessage("Login successful");
        
        return response;
    }
}