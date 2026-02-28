package com.qrrestaurant.backend.dto.Response;

import com.qrrestaurant.backend.entity.ActivityLog;
import java.time.LocalDateTime;

public class ActivityLogResponse {
    private Long id;
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
    private LocalDateTime createdAt;
    private String timeAgo;
    private String metadata;

    public ActivityLogResponse() {}

    public ActivityLogResponse(ActivityLog log) {
        this.id = log.getId();
        this.type = log.getType();
        this.action = log.getAction();
        this.description = log.getDescription();
        this.entityType = log.getEntityType();
        this.entityId = log.getEntityId();
        this.performedBy = log.getPerformedBy();
        this.performedById = log.getPerformedById();
        this.oldValue = log.getOldValue();
        this.newValue = log.getNewValue();
        this.ipAddress = log.getIpAddress();
        this.createdAt = log.getCreatedAt();
        this.timeAgo = log.getTimeAgo();
        this.metadata = log.getMetadata();
    }

    // Factory method
    public static ActivityLogResponse fromEntity(ActivityLog log) {
        return new ActivityLogResponse(log);
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

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}