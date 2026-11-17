package com.example.project.repositories;

import com.example.project.model.EmployeeInvitation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeInvitationRepository extends JpaRepository<EmployeeInvitation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM EmployeeInvitation i WHERE i.tokenHash = :tokenHash")
    Optional<EmployeeInvitation> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    // Check whether a pending invitation already exists
    boolean existsByEmailAndRestaurant_IdAndAcceptedFalse(
            String email,
            Long restaurantId
    );
}
