package com.example.project.repositories;

import com.example.project.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRestaurantRepository extends JpaRepository<UserRestaurant, Long> {

    boolean existsByRole(Role role);

    Optional<UserRestaurant> findByUserAndRestaurant(User user, Restaurants restaurant);

    List<UserRestaurant> findByRestaurant(Restaurants restaurant);

    List<UserRestaurant> findByUser(User user);

    boolean existsByUserAndRestaurant(User user, Restaurants restaurant);

    boolean existsByRestaurant(Restaurants restaurant);

    boolean existsByUser_IdAndRestaurant_IdAndRole_RoleName(Long ownerId, Long id, RoleType roleName);
}
