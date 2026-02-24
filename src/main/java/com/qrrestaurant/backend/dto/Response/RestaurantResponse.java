package com.qrrestaurant.backend.dto.Response;

import com.qrrestaurant.backend.entity.Restaurant;
import com.qrrestaurant.backend.entity.RestaurantStatus;
import java.time.LocalDateTime;

public class RestaurantResponse {

    private Long id;
    private String name;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private RestaurantStatus status;
    private String slug;
    private String customDomain;
    private LocalDateTime createdAt;

    // Private constructor for builder
    private RestaurantResponse(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.ownerName = builder.ownerName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
        this.city = builder.city;
        this.state = builder.state;
        this.pincode = builder.pincode;
        this.status = builder.status;
        this.slug = builder.slug;
        this.customDomain = builder.customDomain;
        this.createdAt = builder.createdAt;
    }

    // Builder class
    public static class Builder {
        private Long id;
        private String name;
        private String ownerName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private RestaurantStatus status;
        private String slug;
        private String customDomain;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder pincode(String pincode) { this.pincode = pincode; return this; }
        public Builder status(RestaurantStatus status) { this.status = status; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder customDomain(String customDomain) { this.customDomain = customDomain; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RestaurantResponse build() {
            return new RestaurantResponse(this);
        }
    }

    public static Builder builder() { return new Builder(); }

    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        return builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .ownerName(restaurant.getOwnerName())
                .email(restaurant.getEmail())
                .phone(restaurant.getPhone())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .state(restaurant.getState())
                .pincode(restaurant.getPincode())
                .status(restaurant.getStatus())
                .slug(restaurant.getSlug())
                .customDomain(restaurant.getCustomDomain())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getOwnerName() { return ownerName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
    public RestaurantStatus getStatus() { return status; }
    public String getSlug() { return slug; }
    public String getCustomDomain() { return customDomain; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}