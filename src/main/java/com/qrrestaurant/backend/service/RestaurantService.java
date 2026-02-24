package com.qrrestaurant.backend.service;


import java.time.LocalDateTime;
import java.util.List;



import com.qrrestaurant.backend.dto.Response.RestaurantResponse;
import com.qrrestaurant.backend.dto.request.RegisterRestaurantRequest;
import com.qrrestaurant.backend.dto.request.UpdateRestaurantRequest;

import org.springframework.data.domain.Sort;        // ✅ CORRECT
 // ✅ CORRECT
import org.springframework.data.domain.Pageable;    // ✅
import org.springframework.data.domain.Page;        // ✅ CORRECT

public interface RestaurantService {
    RestaurantResponse registerRestaurant(RegisterRestaurantRequest request);
    RestaurantResponse approveRestaurant(Long id);
    RestaurantResponse rejectRestaurant(Long id, String reason);
    
    
    RestaurantResponse suspendRestaurant(Long id, String reason);
    RestaurantResponse reopenRestaurant(Long id);
    
    List<RestaurantResponse> searchWithFilters(
            String name, String ownerName, String email, String phone, 
            String gstNumber, String city, String state, String status);
        
        // Get by status with sorting
        
    List<RestaurantResponse> searchRestaurants(String query);
        // Get by date range
        List<RestaurantResponse> getRestaurantsByDateRange(LocalDateTime start, LocalDateTime end);
        
        // Get with pagination
        
        List<RestaurantResponse> getRestaurantsByStatus(String status, Sort sort);
        Page<RestaurantResponse> getRestaurantsPaginated(Pageable pageable);
        // Get by ID
        RestaurantResponse getRestaurantById(Long id);
        
        RestaurantResponse updateRestaurant(Long id, UpdateRestaurantRequest request);
}