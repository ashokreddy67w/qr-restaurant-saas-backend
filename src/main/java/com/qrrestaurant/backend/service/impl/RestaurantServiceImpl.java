package com.qrrestaurant.backend.service.impl;

import com.qrrestaurant.backend.dto.Response.RestaurantResponse;
import com.qrrestaurant.backend.dto.request.RegisterRestaurantRequest;
import com.qrrestaurant.backend.dto.request.UpdateRestaurantRequest;
import com.qrrestaurant.backend.entity.Activity;
import com.qrrestaurant.backend.entity.PlatformAdmin;
import com.qrrestaurant.backend.entity.Restaurant;
import com.qrrestaurant.backend.entity.RestaurantOwner;
import com.qrrestaurant.backend.entity.RestaurantStatus;
import com.qrrestaurant.backend.exception.ResourceNotFoundException;

import com.qrrestaurant.backend.repository.ActivityRepository;

import com.qrrestaurant.backend.repository.RestaurantOwnerRepository;
import com.qrrestaurant.backend.repository.RestaurantRepository;
import com.qrrestaurant.backend.service.EmailService;
import com.qrrestaurant.backend.service.RestaurantService;
import com.qrrestaurant.backend.service.ActivityLogService;  // ✅ ADD THIS IMPORT
import com.qrrestaurant.backend.util.FileStorageUtil;
import com.qrrestaurant.backend.util.SlugGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;



import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor 
public class RestaurantServiceImpl implements RestaurantService {

	

    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    
    // Repositories and services
    private final RestaurantRepository restaurantRepository;
    
    private final EmailService emailService;
    private final FileStorageUtil fileStorageUtil;
    private final SlugGenerator slugGenerator;
    private final ActivityRepository activityRepository;
    private final ActivityLogService activityLogService;  // ✅ ADD THIS FIELD
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final PasswordEncoder passwordEncoder;

      // ✅ Match the type

    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            EmailService emailService,
            FileStorageUtil fileStorageUtil,
            SlugGenerator slugGenerator,
            ActivityRepository activityRepository,
            ActivityLogService activityLogService,
            RestaurantOwnerRepository restaurantOwnerRepository,
            PasswordEncoder passwordEncoder
            ) {  // ✅ Parameter type matches
        
        this.restaurantRepository = restaurantRepository;
        this.emailService = emailService;
        this.fileStorageUtil = fileStorageUtil;
        this.slugGenerator = slugGenerator;
        this.activityRepository = activityRepository;
        this.activityLogService = activityLogService;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Send confirmation email to owner
        emailService.sendRestaurantRegistrationConfirmation(saved.getEmail(), saved.getName());
        
        // ✅ LOG ACTIVITY - Restaurant Registration
        try {
            activityLogService.logRestaurantRegistration(
                saved.getId(),
                saved.getName(),
                saved.getEmail()
            );
            log.debug("Registration activity logged for restaurant: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to log registration activity: {}", e.getMessage());
        }

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
        
        // ✅ CREATE OWNER ACCOUNT (this generates password, saves owner, and sends credentials email)
        
        // ✅ Create owner account and get the raw password
        String rawPassword = createOwnerAccount(saved);
        
        // ✅ Send a single approval email that includes the credentials
        try {
        	emailService.sendApprovalEmail(saved, rawPassword); 
        	
        	log.info("Approval email with credentials sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send approval email to {}: {}", saved.getEmail(), e.getMessage());
        }
        
        // ✅ LOG ACTIVITY using ActivityLogService
        try {
            activityLogService.logRestaurantApproval(
                saved.getId(),
                saved.getName(),
                1L, // TODO: Get actual admin ID from SecurityContext
                "admin@example.com" // TODO: Get actual admin email
            );
            log.debug("Approval activity logged for restaurant: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to log approval activity: {}", e.getMessage());
        }
        
        // Keep existing Activity logging (optional - can be removed later)
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
                log.debug("Legacy activity logged for restaurant approval");
            } catch (Exception e) {
                log.error("Failed to log legacy activity: {}", e.getMessage());
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
        
        String oldStatus = restaurant.getStatus().name();
        restaurant.setStatus(RestaurantStatus.REJECTED);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // Send rejection email
        try {
            emailService.sendRejectionEmail(saved, reason);
            log.info("Rejection email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send rejection email to {}: {}", saved.getEmail(), e.getMessage());
        }
        
        // ✅ LOG ACTIVITY using ActivityLogService
        try {
            activityLogService.logRestaurantRejection(
                saved.getId(),
                saved.getName(),
                1L, // TODO: Get actual admin ID
                "admin@example.com", // TODO: Get actual admin email
                reason
            );
            log.debug("Rejection activity logged for restaurant: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to log rejection activity: {}", e.getMessage());
        }
        
        // Keep existing Activity logging
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
            } catch (Exception e) {
                log.error("Failed to log legacy activity: {}", e.getMessage());
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
        
        String oldStatus = restaurant.getStatus().name();
        restaurant.setStatus(RestaurantStatus.SUSPENDED);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // Send suspension email
        try {
            emailService.sendSuspensionEmail(saved, reason);
            log.info("Suspension email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send suspension email: {}", e.getMessage());
        }
        
        // ✅ LOG ACTIVITY using ActivityLogService
        try {
            activityLogService.logRestaurantSuspension(
                saved.getId(),
                saved.getName(),
                1L, // TODO: Get actual admin ID
                "admin@example.com", // TODO: Get actual admin email
                reason
            );
            log.debug("Suspension activity logged for restaurant: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to log suspension activity: {}", e.getMessage());
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse reopenRestaurant(Long id) {
        log.info("Reopening restaurant with id: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        
        String oldStatus = restaurant.getStatus().name();
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant.setUpdatedAt(LocalDateTime.now());
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // Send reopen email
        try {
            emailService.sendReopenEmail(saved);
            log.info("Reopen email sent to: {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Failed to send reopen email: {}", e.getMessage());
        }
        
        // ✅ LOG ACTIVITY using ActivityLogService
        try {
            activityLogService.logRestaurantReopen(
                saved.getId(),
                saved.getName(),
                1L, // TODO: Get actual admin ID
                "admin@example.com" // TODO: Get actual admin email
            );
            log.debug("Reopen activity logged for restaurant: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to log reopen activity: {}", e.getMessage());
        }
        
        return RestaurantResponse.fromEntity(saved);
    }
    
    @Override
    @Transactional
    public RestaurantResponse updateRestaurant(Long id, UpdateRestaurantRequest request) {
        log.info("Updating restaurant with id: {}", id);
        
        // Find the restaurant
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        // Build changes string for logging
        StringBuilder changes = new StringBuilder();
        
        // Update fields only if they are provided (not null)
        if (request.getName() != null && !request.getName().equals(restaurant.getName())) {
            changes.append("Name: ").append(restaurant.getName()).append(" → ").append(request.getName()).append("; ");
            restaurant.setName(request.getName());
        }
        if (request.getOwnerName() != null && !request.getOwnerName().equals(restaurant.getOwnerName())) {
            changes.append("Owner: ").append(restaurant.getOwnerName()).append(" → ").append(request.getOwnerName()).append("; ");
            restaurant.setOwnerName(request.getOwnerName());
        }
        if (request.getEmail() != null) {
            // Check if new email already exists (but not for this restaurant)
            if (!request.getEmail().equals(restaurant.getEmail()) && 
                restaurantRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use by another restaurant");
            }
            if (!request.getEmail().equals(restaurant.getEmail())) {
                changes.append("Email: ").append(restaurant.getEmail()).append(" → ").append(request.getEmail()).append("; ");
                restaurant.setEmail(request.getEmail());
            }
        }
        if (request.getPhone() != null && !request.getPhone().equals(restaurant.getPhone())) {
            changes.append("Phone: ").append(restaurant.getPhone()).append(" → ").append(request.getPhone()).append("; ");
            restaurant.setPhone(request.getPhone());
        }
        if (request.getAddress() != null && !request.getAddress().equals(restaurant.getAddress())) {
            changes.append("Address updated; ");
            restaurant.setAddress(request.getAddress());
        }
        if (request.getCity() != null && !request.getCity().equals(restaurant.getCity())) {
            changes.append("City: ").append(restaurant.getCity()).append(" → ").append(request.getCity()).append("; ");
            restaurant.setCity(request.getCity());
        }
        if (request.getState() != null && !request.getState().equals(restaurant.getState())) {
            changes.append("State: ").append(restaurant.getState()).append(" → ").append(request.getState()).append("; ");
            restaurant.setState(request.getState());
        }
        if (request.getPincode() != null && !request.getPincode().equals(restaurant.getPincode())) {
            changes.append("Pincode updated; ");
            restaurant.setPincode(request.getPincode());
        }
        if (request.getGstNumber() != null && !request.getGstNumber().equals(restaurant.getGstNumber())) {
            changes.append("GST Number updated; ");
            restaurant.setGstNumber(request.getGstNumber());
        }
        if (request.getFssaiLicense() != null && !request.getFssaiLicense().equals(restaurant.getFssaiLicense())) {
            changes.append("FSSAI License updated; ");
            restaurant.setFssaiLicense(request.getFssaiLicense());
        }
        
        // Update timestamp
        restaurant.setUpdatedAt(LocalDateTime.now());
        
        // Save updated restaurant
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant updated successfully: {}", saved.getId());
        
        // ✅ LOG ACTIVITY for update
        if (changes.length() > 0) {
            try {
                activityLogService.logRestaurantUpdate(
                    saved.getId(),
                    saved.getName(),
                    1L, // TODO: Get actual admin ID
                    "admin@example.com", // TODO: Get actual admin email
                    changes.toString()
                );
                log.debug("Update activity logged for restaurant: {}", saved.getId());
            } catch (Exception e) {
                log.error("Failed to log update activity: {}", e.getMessage());
            }
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
//    @Override
//    @Transactional
//    public RestaurantResponse approveRestaurant(Long id, PlatformAdmin admin) {
//        log.info("Admin {} approving restaurant ID: {}", admin.getEmail(), id);
//        
//        Restaurant restaurant = restaurantRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
//        
//        restaurant.setStatus(RestaurantStatus.ACTIVE);
//        restaurant.setApprovedBy(admin);
//        restaurant.setApprovedAt(LocalDateTime.now());
//        
//        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
//        
//        // ✅ THIS IS WHERE OWNER ACCOUNT IS CREATED
//        createOwnerAccount(savedRestaurant);  // ← Calls this method
//        
//        return RestaurantResponse.fromEntity(savedRestaurant);
//    }
    
    private String createOwnerAccount(Restaurant restaurant) {
        log.info("Creating owner account for: {}", restaurant.getEmail());
        
        String rawPassword = generateRandomPassword();
        log.info("Generated temporary password for owner");
        
        String encoded = passwordEncoder.encode(rawPassword);
        
        RestaurantOwner owner = new RestaurantOwner();
        owner.setEmail(restaurant.getEmail());
        owner.setPassword(encoded);
        owner.setName(restaurant.getOwnerName());
        owner.setPhone(restaurant.getPhone());
        owner.setRestaurant(restaurant);
        owner.setActive(true);
        owner.setCreatedAt(LocalDateTime.now());
        
        restaurantOwnerRepository.save(owner);
        log.info("Owner account created with ID: {}", owner.getId());
        
        // ❌ Remove the separate credentials email – we'll send it in approval email
        // emailService.sendRestaurantOwnerCredentials(...);
        
        return rawPassword;  // return the raw password for use in approval email
    }
    // ✅ RANDOM PASSWORD GENERATOR
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
} 
