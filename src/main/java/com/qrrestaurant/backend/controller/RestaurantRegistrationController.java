package com.qrrestaurant.backend.controller;

import com.qrrestaurant.backend.dto.Response.RestaurantResponse;
import com.qrrestaurant.backend.dto.request.RegisterRestaurantRequest;
import com.qrrestaurant.backend.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantRegistrationController {

    private final RestaurantService restaurantService;

    public RestaurantRegistrationController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerRestaurant(@Valid @ModelAttribute RegisterRestaurantRequest request) {
        try {
            RestaurantResponse response = restaurantService.registerRestaurant(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Simple error response class
    static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}