package com.qrrestaurant.backend.dto.Response;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private String role;
    private Long expiresIn;
    private String message;
    private boolean emailVerified;
    
    // Default constructor
    public AuthResponse() {
    }
    
    // Private constructor for builder
    private AuthResponse(Builder builder) {
        this.token = builder.token;
        this.type = builder.type;
        this.id = builder.id;
        this.email = builder.email;
        this.name = builder.name;
        this.role = builder.role;
        this.expiresIn = builder.expiresIn;
        this.message = builder.message;
        this.emailVerified = builder.emailVerified;
    }
    
    // ===== BUILDER CLASS =====
    public static class Builder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String email;
        private String name;
        private String role;
        private Long expiresIn;
        private String message;
        private boolean emailVerified;
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }
        
        public AuthResponse build() {  // ✅ BUILD METHOD
            return new AuthResponse(this);
        }
    }
    
    // Static method to get builder instance
    public static Builder builder() {  // ✅ BUILDER METHOD
        return new Builder();
    }
    
    // ===== GETTERS AND SETTERS =====
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}