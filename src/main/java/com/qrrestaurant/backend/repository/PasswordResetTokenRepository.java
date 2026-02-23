package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.entity.PasswordResetToken;
import com.qrrestaurant.backend.entity.PlatformAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    // Find token by its value
    Optional<PasswordResetToken> findByToken(String token);
    
    // Find unused token for an admin
    Optional<PasswordResetToken> findByAdminAndUsedFalse(PlatformAdmin admin);
    
    // Find token by admin
    Optional<PasswordResetToken> findByAdmin(PlatformAdmin admin);
    
    // Delete all tokens for an admin
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.admin = :admin")
    void deleteByAdmin(@Param("admin") PlatformAdmin admin);
    
    // Delete all expired tokens
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);
    
    // Check if token exists and is valid
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM PasswordResetToken t " +
           "WHERE t.token = :token AND t.used = false AND t.expiryDate > :now")
    boolean isValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
}