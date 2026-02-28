package com.qrrestaurant.backend.dto.Response;

public class RestaurantOwnerAuthResponse {
    private String token;
    private String type;
    private Long id;
    private String email;
    private String name;
    private String role;
    private Long restaurantId;
    private String message;
    
    // ✅ DEFAULT CONSTRUCTOR
    public RestaurantOwnerAuthResponse() {
    }
    
    // ✅ ALL GETTERS AND SETTERS
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
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}