package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.ActivityLogResponse;
import com.qrrestaurant.backend.service.ActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/activities")
@CrossOrigin(origins = "*")
public class ActivityLogController {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogController.class);
    
    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Get recent activities (for dashboard)
     * GET /api/admin/activities/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLogResponse>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching {} recent activities", limit);
        try {
            List<ActivityLogResponse> activities = activityLogService.getRecentActivities(limit);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching recent activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get activities by entity (restaurant, user, etc.)
     * GET /api/admin/activities/entity/RESTAURANT/1
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ActivityLogResponse>> getActivitiesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        log.info("Fetching activities for {}: {}", entityType, entityId);
        try {
            List<ActivityLogResponse> activities = activityLogService.getActivitiesByEntity(entityType, entityId);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get activities by admin
     * GET /api/admin/activities/admin/1
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<ActivityLogResponse>> getActivitiesByAdmin(@PathVariable Long adminId) {
        log.info("Fetching activities by admin: {}", adminId);
        try {
            List<ActivityLogResponse> activities = activityLogService.getActivitiesByAdmin(adminId);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get activities by type
     * GET /api/admin/activities/type/APPROVAL
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ActivityLogResponse>> getActivitiesByType(@PathVariable String type) {
        log.info("Fetching activities by type: {}", type);
        try {
            List<ActivityLogResponse> activities = activityLogService.getActivitiesByType(type);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get activities by date range
     * GET /api/admin/activities/date-range?start=2026-02-01T00:00:00&end=2026-02-28T23:59:59
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<ActivityLogResponse>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("Fetching activities between {} and {}", start, end);
        try {
            List<ActivityLogResponse> activities = activityLogService.getActivitiesByDateRange(start, end);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get paginated activities
     * GET /api/admin/activities/page?page=0&size=10&sortBy=createdAt&sortDir=desc
     */
    @GetMapping("/page")
    public ResponseEntity<Page<ActivityLogResponse>> getActivitiesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching activities page: {}, size: {}", page, size);
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ActivityLogResponse> activities = activityLogService.getActivitiesPaginated(pageable);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search activities
     * GET /api/admin/activities/search?q=approved
     */
    @GetMapping("/search")
    public ResponseEntity<List<ActivityLogResponse>> searchActivities(@RequestParam String q) {
        log.info("Searching activities with query: {}", q);
        try {
            List<ActivityLogResponse> activities = activityLogService.searchActivities(q);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error searching activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get activity summary
     * GET /api/admin/activities/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<List<Object[]>> getActivitySummary() {
        log.info("Fetching activity summary");
        try {
            List<Object[]> summary = activityLogService.getActivitySummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error fetching activity summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}