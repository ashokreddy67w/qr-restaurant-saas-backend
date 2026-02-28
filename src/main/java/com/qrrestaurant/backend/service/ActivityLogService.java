package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.dto.Response.ActivityLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogService {
    
    // Log an activity
    ActivityLogResponse logActivity(
        String type,
        String action,
        String description,
        String entityType,
        Long entityId,
        Long performedById,
        String performedBy,
        String oldValue,
        String newValue,
        String ipAddress,
        String metadata
    );
    
    // Convenience methods for common actions
    ActivityLogResponse logRestaurantApproval(Long restaurantId, String restaurantName, Long adminId, String adminEmail);
    ActivityLogResponse logRestaurantRejection(Long restaurantId, String restaurantName, Long adminId, String adminEmail, String reason);
    ActivityLogResponse logRestaurantSuspension(Long restaurantId, String restaurantName, Long adminId, String adminEmail, String reason);
    ActivityLogResponse logRestaurantReopen(Long restaurantId, String restaurantName, Long adminId, String adminEmail);
    ActivityLogResponse logRestaurantRegistration(Long restaurantId, String restaurantName, String ownerEmail);
    ActivityLogResponse logRestaurantUpdate(Long restaurantId, String restaurantName, Long adminId, String adminEmail, String changes);
    
    // Get recent activities
    List<ActivityLogResponse> getRecentActivities(int limit);
    
    // Get activities by entity
    List<ActivityLogResponse> getActivitiesByEntity(String entityType, Long entityId);
    
    // Get activities by admin
    List<ActivityLogResponse> getActivitiesByAdmin(Long adminId);
    
    // Get activities by type
    List<ActivityLogResponse> getActivitiesByType(String type);
    
    // Get activities within date range
    List<ActivityLogResponse> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end);
    
    // Get activities with pagination
    Page<ActivityLogResponse> getActivitiesPaginated(Pageable pageable);
    
    // Search activities
    List<ActivityLogResponse> searchActivities(String query);
    
    // Get activity stats
    List<Object[]> getActivitySummary();
}