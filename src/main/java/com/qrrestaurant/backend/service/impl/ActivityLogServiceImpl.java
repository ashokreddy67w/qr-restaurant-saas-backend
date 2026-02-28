package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.Response.ActivityLogResponse;
import com.qrrestaurant.backend.entity.ActivityLog;
import com.qrrestaurant.backend.repository.ActivityLogRepository;
import com.qrrestaurant.backend.service.ActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);
    
    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public ActivityLogResponse logActivity(
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
            String metadata) {
        
        log.info("Logging activity: {} - {}", type, action);
        
        ActivityLog activityLog = ActivityLog.builder()
                .type(type)
                .action(action)
                .description(description)
                .entityType(entityType)
                .entityId(entityId)
                .performedById(performedById)
                .performedBy(performedBy)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .metadata(metadata)
                .build();
        
        ActivityLog saved = activityLogRepository.save(activityLog);
        log.debug("Activity logged with ID: {}", saved.getId());
        
        return ActivityLogResponse.fromEntity(saved);
    }

    @Override
    public ActivityLogResponse logRestaurantApproval(Long restaurantId, String restaurantName, 
                                                     Long adminId, String adminEmail) {
        String description = String.format("Restaurant '%s' was approved", restaurantName);
        return logActivity(
            "APPROVAL",
            "Restaurant Approved",
            description,
            "RESTAURANT",
            restaurantId,
            adminId,
            adminEmail,
            null,
            "APPROVED",
            null,
            null
        );
    }

    @Override
    public ActivityLogResponse logRestaurantRejection(Long restaurantId, String restaurantName, 
                                                      Long adminId, String adminEmail, String reason) {
        String description = String.format("Restaurant '%s' was rejected", restaurantName);
        return logActivity(
            "REJECTION",
            "Restaurant Rejected",
            description,
            "RESTAURANT",
            restaurantId,
            adminId,
            adminEmail,
            null,
            "REJECTED: " + reason,
            null,
            reason
        );
    }

    @Override
    public ActivityLogResponse logRestaurantSuspension(Long restaurantId, String restaurantName, 
                                                       Long adminId, String adminEmail, String reason) {
        String description = String.format("Restaurant '%s' was suspended", restaurantName);
        return logActivity(
            "SUSPENSION",
            "Restaurant Suspended",
            description,
            "RESTAURANT",
            restaurantId,
            adminId,
            adminEmail,
            "ACTIVE",
            "SUSPENDED",
            null,
            reason
        );
    }

    @Override
    public ActivityLogResponse logRestaurantReopen(Long restaurantId, String restaurantName, 
                                                   Long adminId, String adminEmail) {
        String description = String.format("Restaurant '%s' was reopened", restaurantName);
        return logActivity(
            "REOPEN",
            "Restaurant Reopened",
            description,
            "RESTAURANT",
            restaurantId,
            adminId,
            adminEmail,
            "SUSPENDED",
            "ACTIVE",
            null,
            null
        );
    }

    @Override
    public ActivityLogResponse logRestaurantRegistration(Long restaurantId, String restaurantName, 
                                                         String ownerEmail) {
        String description = String.format("New restaurant '%s' registered", restaurantName);
        return logActivity(
            "REGISTRATION",
            "Restaurant Registered",
            description,
            "RESTAURANT",
            restaurantId,
            null,
            ownerEmail,
            null,
            "PENDING",
            null,
            null
        );
    }

    @Override
    public ActivityLogResponse logRestaurantUpdate(Long restaurantId, String restaurantName, 
                                                   Long adminId, String adminEmail, String changes) {
        String description = String.format("Restaurant '%s' was updated", restaurantName);
        return logActivity(
            "UPDATE",
            "Restaurant Updated",
            description,
            "RESTAURANT",
            restaurantId,
            adminId,
            adminEmail,
            null,
            changes,
            null,
            null
        );
    }

    @Override
    public List<ActivityLogResponse> getRecentActivities(int limit) {
        log.debug("Fetching {} recent activities", limit);
        List<ActivityLog> activities = activityLogRepository.findTop10ByOrderByCreatedAtDesc();
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponse> getActivitiesByEntity(String entityType, Long entityId) {
        log.debug("Fetching activities for {}: {}", entityType, entityId);
        List<ActivityLog> activities = activityLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId);
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponse> getActivitiesByAdmin(Long adminId) {
        log.debug("Fetching activities by admin: {}", adminId);
        List<ActivityLog> activities = activityLogRepository.findByPerformedByIdOrderByCreatedAtDesc(adminId);
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponse> getActivitiesByType(String type) {
        log.debug("Fetching activities by type: {}", type);
        List<ActivityLog> activities = activityLogRepository.findByTypeOrderByCreatedAtDesc(type);
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponse> getActivitiesByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching activities between {} and {}", start, end);
        List<ActivityLog> activities = activityLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityLogResponse> getActivitiesPaginated(Pageable pageable) {
        log.debug("Fetching activities page: {}", pageable.getPageNumber());
        Page<ActivityLog> page = activityLogRepository.findAll(pageable);
        return page.map(ActivityLogResponse::fromEntity);
    }

    @Override
    public List<ActivityLogResponse> searchActivities(String query) {
        log.debug("Searching activities with query: {}", query);
        List<ActivityLog> activities = activityLogRepository.searchByDescription(query);
        return activities.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getActivitySummary() {
        log.debug("Fetching activity summary");
        return activityLogRepository.getActivitySummary();
    }
}