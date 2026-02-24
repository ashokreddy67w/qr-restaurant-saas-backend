package com.qrrestaurant.backend.service;



import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.dto.Response.AuthResponse;
import com.qrrestaurant.backend.dto.request.LoginRequest;

public interface AuthService {
    
    AuthResponse register(PlatformAdmin admin);
    
    AuthResponse login(LoginRequest request);
    
    boolean validateToken(String token);
}