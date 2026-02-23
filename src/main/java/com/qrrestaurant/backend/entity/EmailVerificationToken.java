package com.qrrestaurant.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private PlatformAdmin admin;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    private boolean used = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // 24 hours expiry
    public static final int EXPIRATION_HOURS = 24;
    
    // Default constructor
    public EmailVerificationToken() {
    }
    
    // Constructor with admin
    public EmailVerificationToken(PlatformAdmin admin) {
        this.admin = admin;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        this.used = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // ===== GETTERS AND SETTERS =====
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {  // ✅ ADD THIS
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public PlatformAdmin getAdmin() {  // ✅ ADD THIS
        return admin;
    }
    
    public void setAdmin(PlatformAdmin admin) {
        this.admin = admin;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public boolean isUsed() {  // ✅ ADD THIS
        return used;
    }
    
    public void setUsed(boolean used) {  // ✅ ADD THIS
        this.used = used;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper method to check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}