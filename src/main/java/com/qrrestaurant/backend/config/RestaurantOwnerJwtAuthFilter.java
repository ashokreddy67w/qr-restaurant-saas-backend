package com.qrrestaurant.backend.config;

import com.qrrestaurant.backend.service.OwnerUserDetailsService;
import com.qrrestaurant.backend.service.RestaurantOwnerJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RestaurantOwnerJwtAuthFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(RestaurantOwnerJwtAuthFilter.class);
    
    private final RestaurantOwnerJwtService ownerJwtService;
    private final OwnerUserDetailsService ownerDetailsService;
    
    // ✅ Explicit constructor
    public RestaurantOwnerJwtAuthFilter(
            RestaurantOwnerJwtService ownerJwtService,
            OwnerUserDetailsService ownerDetailsService) {
        this.ownerJwtService = ownerJwtService;
        this.ownerDetailsService = ownerDetailsService;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String ownerEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        ownerEmail = ownerJwtService.extractEmail(jwt);
        
        if (ownerEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.ownerDetailsService.loadUserByUsername(ownerEmail);
            
            if (ownerJwtService.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Owner authenticated: {}", ownerEmail);
            }
        }
        filterChain.doFilter(request, response);
    }
}