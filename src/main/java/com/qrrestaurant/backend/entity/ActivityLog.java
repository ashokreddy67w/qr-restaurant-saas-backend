package com.qrrestaurant.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // APPROVAL, REJECTION, SUSPEND, REOPEN, REGISTRATION, UPDATE

    @Column(nullable = false)
    private String action; // "Restaurant approved", "Restaurant rejected", etc.

    @Column(nullable = false)
    private String description; // "Spice Kitchen was approved by admin"

    @Column(name = "entity_type", nullable = false)
    private String entityType; // "RESTAURANT", "USER", "TICKET"

    @Column(name = "entity_id")
    private Long entityId; // ID of the restaurant/user/ticket

    @Column(name = "performed_by")
    private String performedBy; // Email or name of admin who performed action

    @Column(name = "performed_by_id")
    private Long performedById; // ID of admin who performed action

    @Column(name = "old_value", length = 1000)
    private String oldValue; // JSON or string representation of old value

    @Column(name = "new_value", length = 1000)
    private String newValue; // JSON or string representation of new value

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "metadata", length = 2000)
    private String metadata; // Additional data as JSON

    // Constructors
    public ActivityLog() {
        this.createdAt = LocalDateTime.now();
    }

    public ActivityLog(String type, String action, String description, String entityType, Long entityId) {
        this.type = type;
        this.action = action;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = LocalDateTime.now();
    }

    // Builder pattern
    public static class Builder {
        private String type;
        private String action;
        private String description;
        private String entityType;
        private Long entityId;
        private String performedBy;
        private Long performedById;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private String metadata;

        public Builder type(String type) { this.type = type; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder entityType(String entityType) { this.entityType = entityType; return this; }
        public Builder entityId(Long entityId) { this.entityId = entityId; return this; }
        public Builder performedBy(String performedBy) { this.performedBy = performedBy; return this; }
        public Builder performedById(Long performedById) { this.performedById = performedById; return this; }
        public Builder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public Builder newValue(String newValue) { this.newValue = newValue; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }

        public ActivityLog build() {
            ActivityLog log = new ActivityLog();
            log.type = this.type;
            log.action = this.action;
            log.description = this.description;
            log.entityType = this.entityType;
            log.entityId = this.entityId;
            log.performedBy = this.performedBy;
            log.performedById = this.performedById;
            log.oldValue = this.oldValue;
            log.newValue = this.newValue;
            log.ipAddress = this.ipAddress;
            log.metadata = this.metadata;
            return log;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public Long getPerformedById() { return performedById; }
    public void setPerformedById(Long performedById) { this.performedById = performedById; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Helper method to get time ago string
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + " mins ago";
        if (minutes < 1440) return (minutes / 60) + " hours ago";
        return (minutes / 1440) + " days ago";
    }
}