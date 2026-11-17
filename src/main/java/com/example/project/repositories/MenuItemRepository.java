package com.example.project.repositories;

import com.example.project.model.MenuItem;
import com.example.project.model.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantId(Long restaurantId);

    Optional<MenuItem> findByRestaurantAndMenuName(
            Restaurants restaurant,
            String menuName
    );

    List<MenuItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);
}
