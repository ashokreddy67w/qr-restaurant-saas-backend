package com.qrrestaurant.backend.repository;



import com.qrrestaurant.backend.entity.PlatformAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlatformAdminRepository extends JpaRepository<PlatformAdmin, Long> {
    
    Optional<PlatformAdmin> findByEmail(String email);
    
    boolean existsByEmail(String email);
}