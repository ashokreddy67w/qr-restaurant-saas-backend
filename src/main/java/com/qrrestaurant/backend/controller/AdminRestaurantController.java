package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.RestaurantResponse;
import com.qrrestaurant.backend.dto.request.RejectionRequest;
import com.qrrestaurant.backend.dto.request.SuspensionRequest;
import com.qrrestaurant.backend.dto.request.UpdateRestaurantRequest;
import com.qrrestaurant.backend.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    @Autowired 
    private RestaurantService restaurantService;

    // ========== 1. STATUS MANAGEMENT ENDPOINTS ==========

    /**
     * Approve restaurant (PENDING → ACTIVE)
     * POST /api/admin/restaurants/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable Long id) {
        try {
            RestaurantResponse response = restaurantService.approveRestaurant(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Reject restaurant (PENDING → REJECTED)
     * POST /api/admin/restaurants/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<RestaurantResponse> rejectRestaurant(
            @PathVariable Long id,
            @RequestBody(required = false) RejectionRequest request) {
        try {
            String reason = (request != null) ? request.getReason() : null;
            RestaurantResponse response = restaurantService.rejectRestaurant(id, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Suspend restaurant (ACTIVE → SUSPENDED)
     * POST /api/admin/restaurants/{id}/suspend
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<RestaurantResponse> suspendRestaurant(
            @PathVariable Long id,
            @RequestBody(required = false) SuspensionRequest request) {
        try {
            String reason = (request != null) ? request.getReason() : null;
            RestaurantResponse response = restaurantService.suspendRestaurant(id, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Reopen restaurant (SUSPENDED → ACTIVE)
     * POST /api/admin/restaurants/{id}/reopen
     */
    @PostMapping("/{id}/reopen")
    public ResponseEntity<RestaurantResponse> reopenRestaurant(@PathVariable Long id) {
        try {
            RestaurantResponse response = restaurantService.reopenRestaurant(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== 2. GET LISTS BY STATUS ==========

    /**
     * Get all pending restaurants
     * GET /api/admin/restaurants/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<RestaurantResponse>> getPendingRestaurants() {
        try {
            List<RestaurantResponse> response = restaurantService.getRestaurantsByStatus(
                "PENDING", Sort.by(Sort.Direction.DESC, "createdAt"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all approved restaurants
     * GET /api/admin/restaurants/approved
     */
    @GetMapping("/approved")
    public ResponseEntity<List<RestaurantResponse>> getApprovedRestaurants() {
        try {
            List<RestaurantResponse> response = restaurantService.getRestaurantsByStatus(
                "ACTIVE", Sort.by(Sort.Direction.DESC, "createdAt"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all rejected restaurants
     * GET /api/admin/restaurants/rejected
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<RestaurantResponse>> getRejectedRestaurants() {
        try {
            List<RestaurantResponse> response = restaurantService.getRestaurantsByStatus(
                "REJECTED", Sort.by(Sort.Direction.DESC, "createdAt"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all suspended restaurants
     * GET /api/admin/restaurants/suspended
     */
    @GetMapping("/suspended")
    public ResponseEntity<List<RestaurantResponse>> getSuspendedRestaurants() {
        try {
            List<RestaurantResponse> response = restaurantService.getRestaurantsByStatus(
                "SUSPENDED", Sort.by(Sort.Direction.DESC, "createdAt"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== 3. SEARCH ENDPOINTS ==========

    /**
     * Basic search across all fields
     * GET /api/admin/restaurants/search?q=spice
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @RequestParam String q) {
        try {
            List<RestaurantResponse> response = restaurantService.searchRestaurants(q);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Advanced search with filters
     * GET /api/admin/restaurants/search/filter?name=spice&city=mumbai&status=PENDING
     */
    @GetMapping("/search/filter")
    public ResponseEntity<List<RestaurantResponse>> searchWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String gstNumber,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String status) {
        try {
            List<RestaurantResponse> response = restaurantService.searchWithFilters(
                name, ownerName, email, phone, gstNumber, city, state, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== 4. SORTING AND PAGINATION ==========

    /**
     * Get restaurants by status with sorting
     * GET /api/admin/restaurants/status/PENDING?sortBy=createdAt&sortDir=desc
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            List<RestaurantResponse> response = restaurantService.getRestaurantsByStatus(status, sort);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get restaurants by date range
     * GET /api/admin/restaurants/date-range?start=2026-02-01T00:00:00&end=2026-02-28T23:59:59
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<RestaurantResponse> response = restaurantService.getRestaurantsByDateRange(start, end);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get paginated restaurants
     * GET /api/admin/restaurants/page?page=0&size=10&sortBy=name&sortDir=asc
     */
    @GetMapping("/page")
    public ResponseEntity<Page<RestaurantResponse>> getRestaurantsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<RestaurantResponse> response = restaurantService.getRestaurantsPaginated(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== 5. SINGLE RESTAURANT OPERATIONS ==========

    /**
     * Get restaurant by ID
     * GET /api/admin/restaurants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        try {
            RestaurantResponse response = restaurantService.getRestaurantById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Edit restaurant details
     * PUT /api/admin/restaurants/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @RequestBody UpdateRestaurantRequest request) {
        try {
            RestaurantResponse response = restaurantService.updateRestaurant(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}