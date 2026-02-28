package com.qrrestaurant.backend.service;

import com.qrrestaurant.backend.repository.RestaurantOwnerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OwnerUserDetailsService implements UserDetailsService {
    
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    
    // ✅ Explicit constructor
    public OwnerUserDetailsService(RestaurantOwnerRepository restaurantOwnerRepository) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return restaurantOwnerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Owner not found with email: " + email));
    }
}