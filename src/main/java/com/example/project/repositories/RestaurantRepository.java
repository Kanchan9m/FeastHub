package com.example.project.repositories;

import com.example.project.model.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurants, Long> {

//    Optional<Restaurants> findByEmail(String email);

    Optional<Restaurants> findByRestaurantName(String restaurantName);

//    boolean existsByEmail(String email);

    boolean existsByRestaurantName(String restaurantName);

    List<Restaurants> findByStaff_User_Id(Long ownerId);

    long countByStaff_User_Id(Long ownerId);

//    Optional<Restaurants> findByIdAndOwnerId(Long restaurantId, Long ownerId);
}
