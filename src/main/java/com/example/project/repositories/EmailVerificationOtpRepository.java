package com.example.project.repositories;

import com.example.project.model.EmailVerificationOtp;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationOtpRepository  extends JpaRepository<EmailVerificationOtp, Long> {

    Optional<EmailVerificationOtp>
    findTopByUserOrderByCreatedAtDesc(User user);

    @Modifying
    @Query("""
            UPDATE EmailVerificationOtp e
            SET e.used = true
            WHERE e.user = :user
              AND e.used = false
            """)
    void invalidatePreviousOtps(@Param("user") User user);
}
