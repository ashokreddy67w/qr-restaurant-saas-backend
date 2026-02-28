package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Find by entity type
    List<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);
    
    // Find by entity ID
    List<ActivityLog> findByEntityIdOrderByCreatedAtDesc(Long entityId);
    
    // Find by performed by
    List<ActivityLog> findByPerformedByIdOrderByCreatedAtDesc(Long performedById);
    
    // Find by type (APPROVAL, REJECTION, etc.)
    List<ActivityLog> findByTypeOrderByCreatedAtDesc(String type);
    
    // Get recent activities (last 10)
    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();
    
    // Get recent activities by entity type
    List<ActivityLog> findTop10ByEntityTypeOrderByCreatedAtDesc(String entityType);
    
    // Get activities within date range
    List<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime start, LocalDateTime end);
    
    // Get activities by performed by and date range
    List<ActivityLog> findByPerformedByIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        Long performedById, LocalDateTime start, LocalDateTime end);
    
    // Count activities by type
    Long countByType(String type);
    
    // Count activities by entity type
    Long countByEntityType(String entityType);
    
    // Search activities by description containing text
    @Query("SELECT a FROM ActivityLog a WHERE LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<ActivityLog> searchByDescription(@Param("search") String search);
    
    // Get activities with pagination
    Page<ActivityLog> findAll(Pageable pageable);
    
    // Get activities by type with pagination
    Page<ActivityLog> findByType(String type, Pageable pageable);
    
    // Get activities by entity type with pagination
    Page<ActivityLog> findByEntityType(String entityType, Pageable pageable);
    
    // Get distinct activity types
    @Query("SELECT DISTINCT a.type FROM ActivityLog a")
    List<String> findDistinctTypes();
    
    // Get distinct entity types
    @Query("SELECT DISTINCT a.entityType FROM ActivityLog a")
    List<String> findDistinctEntityTypes();
    
    // Get activity summary for dashboard
    @Query("SELECT a.type, COUNT(a) FROM ActivityLog a GROUP BY a.type")
    List<Object[]> getActivitySummary();
}