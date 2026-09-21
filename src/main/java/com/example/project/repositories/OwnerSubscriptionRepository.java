package com.example.project.repositories;

import com.example.project.model.OwnerSubscription;
import com.example.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerSubscriptionRepository extends JpaRepository<OwnerSubscription, Long> {

    Optional<OwnerSubscription> findByOwner(User owner);

    Optional<OwnerSubscription> findByOwnerAndActiveTrue(User owner);

}
