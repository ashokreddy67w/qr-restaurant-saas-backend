package com.qrrestaurant.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
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
    
    // Default constructor
    public PasswordResetToken() {
    }
    
    // Parameterized constructor
    public PasswordResetToken(String token, PlatformAdmin admin, LocalDateTime expiryDate) {
        this.token = token;
        this.admin = admin;
        this.expiryDate = expiryDate;
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
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public PlatformAdmin getAdmin() {
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
    
    // ✅ ADD THIS METHOD
    public boolean isUsed() {
        return used;
    }
    
    // ✅ ADD THIS METHOD
    public void setUsed(boolean used) {
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