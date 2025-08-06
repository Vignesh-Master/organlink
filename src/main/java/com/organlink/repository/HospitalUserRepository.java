package com.organlink.repository;

import com.organlink.model.entity.HospitalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for HospitalUser entity
 * Handles database operations for hospital user authentication
 */
@Repository
public interface HospitalUserRepository extends JpaRepository<HospitalUser, Long> {

    /**
     * Find hospital user by user ID
     */
    Optional<HospitalUser> findByUserId(String userId);

    /**
     * Find hospital user by hospital ID
     */
    Optional<HospitalUser> findByHospitalId(Long hospitalId);

    /**
     * Find hospital user by user ID and hospital ID (for authentication)
     */
    Optional<HospitalUser> findByUserIdAndHospitalId(String userId, Long hospitalId);

    /**
     * Find all hospital users by hospital ID
     */
    List<HospitalUser> findAllByHospitalId(Long hospitalId);

    /**
     * Check if user ID exists
     */
    boolean existsByUserId(String userId);

    /**
     * Find active hospital user by user ID
     */
    Optional<HospitalUser> findByUserIdAndIsActiveTrue(String userId);

    /**
     * Count active hospital users
     */
    long countByIsActiveTrue();

    /**
     * Update last login time
     */
    @Modifying
    @Query("UPDATE HospitalUser hu SET hu.lastLogin = :loginTime, hu.failedLoginAttempts = 0 WHERE hu.userId = :userId")
    void updateLastLogin(@Param("userId") String userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * Increment failed login attempts
     */
    @Modifying
    @Query("UPDATE HospitalUser hu SET hu.failedLoginAttempts = hu.failedLoginAttempts + 1 WHERE hu.userId = :userId")
    void incrementFailedLoginAttempts(@Param("userId") String userId);

    /**
     * Lock account until specified time
     */
    @Modifying
    @Query("UPDATE HospitalUser hu SET hu.accountLockedUntil = :lockUntil WHERE hu.userId = :userId")
    void lockAccount(@Param("userId") String userId, @Param("lockUntil") LocalDateTime lockUntil);

    /**
     * Unlock account
     */
    @Modifying
    @Query("UPDATE HospitalUser hu SET hu.accountLockedUntil = null, hu.failedLoginAttempts = 0 WHERE hu.userId = :userId")
    void unlockAccount(@Param("userId") String userId);
}
