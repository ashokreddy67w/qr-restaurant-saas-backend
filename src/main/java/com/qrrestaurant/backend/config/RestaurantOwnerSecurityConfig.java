package com.qrrestaurant.backend.config;

import com.qrrestaurant.backend.service.OwnerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class RestaurantOwnerSecurityConfig {
    
    private final RestaurantOwnerJwtAuthFilter ownerJwtAuthFilter;
    private final OwnerUserDetailsService ownerDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    // ✅ Explicit constructor instead of @RequiredArgsConstructor
    public RestaurantOwnerSecurityConfig(
            RestaurantOwnerJwtAuthFilter ownerJwtAuthFilter,
            OwnerUserDetailsService ownerDetailsService,
            PasswordEncoder passwordEncoder) {
        this.ownerJwtAuthFilter = ownerJwtAuthFilter;
        this.ownerDetailsService = ownerDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Bean
    public SecurityFilterChain ownerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/owner/**")
            .cors(cors -> cors.configurationSource(ownerCorsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/owner/login").permitAll()
                .requestMatchers("/api/owner/forgot-password").permitAll()
                .requestMatchers("/api/owner/reset-password").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().hasRole("OWNER")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(ownerAuthenticationProvider())
            .addFilterBefore(ownerJwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource ownerCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public AuthenticationProvider ownerAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(ownerDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager restaurantAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}