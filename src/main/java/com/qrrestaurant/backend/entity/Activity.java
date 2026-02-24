package com.qrrestaurant.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // APPROVAL, REJECTION, REGISTRATION, etc.

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "time_ago")
    private String timeAgo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "metadata")
    private String metadata;

    // Constructors
    public Activity() {}

    public Activity(String type, String title, String description, Long referenceId, String metadata) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.referenceId = referenceId;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
        updateTimeAgo();
    }

    @PreUpdate
    @PrePersist
    public void updateTimeAgo() {
        if (createdAt != null) {
            Duration duration = Duration.between(createdAt, LocalDateTime.now());
            long minutes = duration.toMinutes();
            
            if (minutes < 1) {
                timeAgo = "just now";
            } else if (minutes < 60) {
                timeAgo = minutes + " mins ago";
            } else if (minutes < 1440) {
                timeAgo = (minutes / 60) + " hours ago";
            } else {
                timeAgo = (minutes / 1440) + " days ago";
            }
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTimeAgo() { return timeAgo; }
    public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
        updateTimeAgo();
    }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}