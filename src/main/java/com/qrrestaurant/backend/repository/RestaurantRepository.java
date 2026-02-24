package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.entity.Restaurant;
import com.qrrestaurant.backend.entity.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // ========== EXISTING METHODS (KEEP ALL) ==========
    Optional<Restaurant> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Restaurant> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Restaurant> findByStatus(RestaurantStatus status);
    
    @Query("SELECT r FROM Restaurant r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> search(@Param("query") String query);

    // ========== METHODS FOR DASHBOARD STATS ==========
    
    // Count new restaurants this month
    Long countByCreatedAtAfter(LocalDateTime date);
    
    // Get pending restaurants (for approval queue)
    List<Restaurant> findByStatusOrderByCreatedAtDesc(RestaurantStatus status);
    
    // ========== ENHANCED SEARCH METHODS ==========
    
    // 1. Basic search across ALL fields (not just name and city)
    @Query("SELECT r FROM Restaurant r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.ownerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.gstNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.state) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Restaurant> searchAllFields(@Param("query") String query);
    
    // 2. Advanced search with filters
    @Query("SELECT r FROM Restaurant r WHERE " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:ownerName IS NULL OR LOWER(r.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) AND " +
           "(:email IS NULL OR LOWER(r.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:phone IS NULL OR LOWER(r.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND " +
           "(:gstNumber IS NULL OR LOWER(r.gstNumber) LIKE LOWER(CONCAT('%', :gstNumber, '%'))) AND " +
           "(:city IS NULL OR LOWER(r.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(r.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:status IS NULL OR r.status = :status)")
    List<Restaurant> searchWithFilters(
        @Param("name") String name,
        @Param("ownerName") String ownerName,
        @Param("email") String email,
        @Param("phone") String phone,
        @Param("gstNumber") String gstNumber,
        @Param("city") String city,
        @Param("state") String state,
        @Param("status") RestaurantStatus status);
    
    // 3. Get by status with sorting
    List<Restaurant> findByStatus(RestaurantStatus status, Sort sort);
    
    // 4. Get by date range
    List<Restaurant> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime start, LocalDateTime end);
    
    // 5. Pagination
    Page<Restaurant> findAll(Pageable pageable);
    
    // 6. Get by status with pagination
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);
    
    // ========== COUNT METHODS ==========
    
    // Count by status
    Long countByStatus(RestaurantStatus status);
    
    // Count by date range
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // ========== FOR TOP RESTAURANTS (if you add orders/revenue later) ==========
    
    // @Query("SELECT r FROM Restaurant r ORDER BY r.totalRevenue DESC")
    // List<Restaurant> findTopRestaurants(Pageable pageable);
}