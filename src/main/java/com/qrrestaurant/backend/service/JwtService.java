package com.qrrestaurant.backend.service;



import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    
    String generateToken(String email, Long adminId, String name);
    
    String extractEmail(String token);
    
    Date extractExpiration(String token);
    
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    
    Boolean isTokenExpired(String token);
    
    Boolean validateToken(String token, String email);
    
    Long getExpirationTime();
}