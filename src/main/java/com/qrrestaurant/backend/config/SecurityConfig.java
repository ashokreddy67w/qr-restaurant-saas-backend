package com.qrrestaurant.backend.config;

import com.qrrestaurant.backend.repository.PlatformAdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final PlatformAdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            PlatformAdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ FIX 1: CORS — allows your frontend (any port) to call the backend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (for development — tighten this in production)
        config.setAllowedOriginPatterns(List.of("*"));

        // Allow all standard HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow Authorization header (needed for JWT) + Content-Type
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies / Authorization header)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);   // applies to ALL endpoints
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> adminRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    @Primary
    public AuthenticationManager platformAdminAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ FIX 2: Wire in the CORS bean defined above
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no token needed
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/password/**").permitAll()
                .requestMatchers("/api/restaurants/register").permitAll()

                // ✅ FIX 3: OPTIONS preflight requests must ALWAYS be permitted
                // (browser sends OPTIONS before every cross-origin POST/PUT)
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                // Admin endpoints — requires valid JWT with ROLE_ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()
            )

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())

            // ✅ FIX 4: JWT filter runs BEFORE Spring's auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}