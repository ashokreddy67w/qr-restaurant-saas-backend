package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.Response.RestaurantResponse;
import com.qrrestaurant.backend.dto.request.RegisterRestaurantRequest;
import com.qrrestaurant.backend.dto.request.UpdateRestaurantRequest;
import com.qrrestaurant.backend.entity.Activity;
import com.qrrestaurant.backend.entity.Restaurant;
import com.qrrestaurant.backend.entity.RestaurantStatus;
import com.qrrestaurant.backend.repository.ActivityRepository;
import com.qrrestaurant.backend.repository.RestaurantRepository;
import com.qrrestaurant.backend.service.EmailService;
import com.qrrestaurant.backend.service.RestaurantService;
import com.qrrestaurant.backend.util.FileStorageUtil;
import com.qrrestaurant.backend.util.SlugGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;        // ✅ CORRECT
import org.springframework.data.domain.Pageable;    // ✅ CORRECT
import org.springframework.data.domain.Page;        // ✅ CORRECT



import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    
    // ✅ ONLY these repositories and services
    private final RestaurantRepository restaurantRepository;
    private final EmailService emailService;        // This calls email, doesn't send directly
    private final FileStorageUtil fileStorageUtil;
    private final SlugGenerator slugGenerator;
    private final ActivityRepository activityRepository; 

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 EmailService emailService,
                                 FileStorageUtil fileStorageUtil,
                                 SlugGenerator slugGenerator,
                                 ActivityRepository activityRepository) {
        this.restaurantRepository = restaurantRepository;
        this.emailService = emailService;
        this.fileStorageUtil = fileStorageUtil;
        this.slugGenerator = slugGenerator;
        this.activityRepository = activityRepository;
    }

    @Override
    @Transactional
    public RestaurantResponse registerRestaurant(RegisterRestaurantRequest request) {
        // Check if email already exists
        if (restaurantRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Restaurant with this email already exists");
        }

        // Generate unique slug from name + city
        String baseSlug = slugGenerator.generateSlug(request.getName(), request.getCity());
        String uniqueSlug = baseSlug;
        int counter = 1;
        while (restaurantRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter++;
        }

        // Save uploaded files
        String logoPath = null;
        String gstPath = null;
        String fssaiPath = null;
        try {
            if (request.getLogo() != null && !request.getLogo().isEmpty()) {
                logoPath = fileStorageUtil.storeFile(request.getLogo(), "logos");
            }
            if (request.getGstCertificate() != null && !request.getGstCertificate().isEmpty()) {
                gstPath = fileStorageUtil.storeFile(request.getGstCertificate(), "gst");
            }
            if (request.getFssaiCertificate() != null && !request.getFssaiCertificate().isEmpty()) {
                fssaiPath = fileStorageUtil.storeFile(request.getFssaiCertificate(), "fssai");
            }
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        // Create restaurant entity
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setOwnerName(request.getOwnerName());
        restaurant.setEmail(request.getEmail());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setState(request.getState());
        restaurant.setPincode(request.getPincode());
        restaurant.setGstNumber(request.getGstNumber());
        restaurant.setFssaiLicense(request.getFssaiLicense());
        restaurant.setLogoPath(logoPath);
        restaurant.setGstCertificatePath(gstPath);
        restaurant.setFssaiCertificatePath(fssaiPath);
        restaurant.setSlug(uniqueSlug);
        restaurant.setStatus(RestaurantStatus.PENDING);
        restaurant.setCreatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);

        // Send confirmation email to owner (via EmailService)
        emailService.sendRestaurantRegistrationConfirmation(saved.getEmail(), saved.getName());

        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse approveRestaurant(Long id) {
        log.info("Approving restaurant with id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // ✅ Email is sent via EmailService, NOT directly
        try {
            emailService.sendApprovalEmail(saved);
            log.info("Approval email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send approval email to {}: {}", saved.getEmail(), e.getMessage());
        }
        
        // Log activity
        if (activityRepository != null) {
            try {
                Activity activity = new Activity(
                    "APPROVAL",
                    "Restaurant approved",
                    saved.getName(),
                    saved.getId(),
                    null
                );
                activityRepository.save(activity);
                log.debug("Activity logged for restaurant approval");
            } catch (Exception e) {
                log.error("Failed to log activity: {}", e.getMessage());
            }
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse rejectRestaurant(Long id, String reason) {
        log.info("Rejecting restaurant with id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // ✅ Email is sent via EmailService
        try {
            emailService.sendRejectionEmail(saved, reason);
            log.info("Rejection email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send rejection email to {}: {}", saved.getEmail(), e.getMessage());
        }
        
        // Log activity
        if (activityRepository != null) {
            try {
                Activity activity = new Activity(
                    "REJECTION",
                    "Restaurant rejected",
                    saved.getName(),
                    saved.getId(),
                    reason
                );
                activityRepository.save(activity);
                log.debug("Activity logged for restaurant rejection");
            } catch (Exception e) {
                log.error("Failed to log activity: {}", e.getMessage());
            }
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse suspendRestaurant(Long id, String reason) {
        log.info("Suspending restaurant with id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        restaurant.setStatus(RestaurantStatus.SUSPENDED);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // ✅ Email is sent via EmailService
        try {
            emailService.sendSuspensionEmail(saved, reason);
            log.info("Suspension email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send suspension email: {}", e.getMessage());
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse reopenRestaurant(Long id) {
        log.info("Reopening restaurant with id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // ✅ Email is sent via EmailService
        try {
            emailService.sendReopenEmail(saved);
            log.info("Reopen email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send reopen email: {}", e.getMessage());
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    public List<RestaurantResponse> searchRestaurants(String query) {
        log.info("Searching restaurants with query: {}", query);
        List<Restaurant> restaurants = restaurantRepository.searchAllFields(query);
        return restaurants.stream()
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> searchWithFilters(
            String name, String ownerName, String email, String phone, 
            String gstNumber, String city, String state, String status) {
        
        log.info("Searching with filters - name: {}, city: {}, status: {}", name, city, status);
        
        // Convert string status to enum if provided
        RestaurantStatus restaurantStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                restaurantStatus = RestaurantStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", status);
            }
        }
        
        List<Restaurant> restaurants = restaurantRepository.searchWithFilters(
            name, ownerName, email, phone, gstNumber, city, state, restaurantStatus);
        
        return restaurants.stream()
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> getRestaurantsByStatus(String status, Sort sort) { 
        log.info("Getting restaurants by status: {}, sort: {}", status, sort);
        
        RestaurantStatus restaurantStatus = RestaurantStatus.valueOf(status.toUpperCase());
        List<Restaurant> restaurants = restaurantRepository.findByStatus(restaurantStatus, sort);
        
        return restaurants.stream()
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponse> getRestaurantsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Getting restaurants between {} and {}", start, end);
        
        List<Restaurant> restaurants = restaurantRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
        
        return restaurants.stream()
                .map(RestaurantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RestaurantResponse> getRestaurantsPaginated(Pageable pageable) {
        log.info("Getting restaurants paginated - page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
        
        return restaurantPage.map(RestaurantResponse::fromEntity);
    }

    @Override
    public RestaurantResponse getRestaurantById(Long id) {
        log.info("Getting restaurant by id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        return RestaurantResponse.fromEntity(restaurant);
    }

    
    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long id, UpdateRestaurantRequest request) {
        log.info("Updating restaurant with id: {}", id);
        
        // Find the restaurant
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        // Update fields only if they are provided (not null)
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getOwnerName() != null) {
            restaurant.setOwnerName(request.getOwnerName());
        }
        if (request.getEmail() != null) {
            // Check if new email already exists (but not for this restaurant)
            if (!request.getEmail().equals(restaurant.getEmail()) && 
                restaurantRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use by another restaurant");
            }
            restaurant.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            restaurant.setCity(request.getCity());
        }
        if (request.getState() != null) {
            restaurant.setState(request.getState());
        }
        if (request.getPincode() != null) {
            restaurant.setPincode(request.getPincode());
        }
        if (request.getGstNumber() != null) {
            restaurant.setGstNumber(request.getGstNumber());
        }
        if (request.getFssaiLicense() != null) {
            restaurant.setFssaiLicense(request.getFssaiLicense());
        }
        
        // Update timestamp
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        // Save updated restaurant
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant updated successfully: {}", saved.getId());
        
        return RestaurantResponse.fromEntity(saved);
    }


}