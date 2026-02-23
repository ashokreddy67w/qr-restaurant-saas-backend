package com.qrrestaurant.backend.repository;

import com.qrrestaurant.backend.entity.EmailVerificationToken;
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
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByToken(String token);
    
    Optional<EmailVerificationToken> findByAdminAndUsedFalse(PlatformAdmin admin);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.admin = :admin")
    void deleteByAdmin(@Param("admin") PlatformAdmin admin);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiryDate < :now")
    void deleteAllExpiredTokens(@Param("now") LocalDateTime now);
}