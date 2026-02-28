package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.entity.RestaurantOwner;
import com.qrrestaurant.backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {
    
    Optional<RestaurantOwner> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<RestaurantOwner> findByRestaurant(Restaurant restaurant);
}