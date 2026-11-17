
package com.example.project.seeder;

import com.example.project.model.MenuItem;
import com.example.project.model.Restaurants;
import com.example.project.model.Role;
import com.example.project.model.RoleType;
import com.example.project.model.User;
import com.example.project.model.UserRestaurant;
import com.example.project.repositories.MenuItemRepository;
import com.example.project.repositories.RestaurantRepository;
import com.example.project.repositories.RoleRepository;
import com.example.project.repositories.UserRepository;
import com.example.project.repositories.UserRestaurantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRestaurantRepository userRestaurantRepository;

    public DataSeeder(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository,
            UserRestaurantRepository userRestaurantRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRestaurantRepository = userRestaurantRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        log.info("========== RMS SEEDER STARTED ==========");

        User owner = userRepository.findByEmail("roni1@gmail.com")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Seed owner roni1@gmail.com was not found"
                        ));

        Role ownerRole = roleRepository
                .findByRoleName(RoleType.ROLE_OWNER)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ROLE_OWNER was not found"
                        ));

        int restaurantsCreated = 0;
        int restaurantsReused = 0;
        int menuItemsCreated = 0;
        int menuItemsUpdated = 0;

        for (var data : RestaurantSeedData.getRestaurants()) {

            // 1. Find an existing seeded restaurant or create one.
            Restaurants restaurant = restaurantRepository
                    .findByRestaurantNameAndSeededTrue(data.name())
                    .orElse(null);

            if (restaurant == null) {

                restaurant = new Restaurants();

                restaurantsCreated++;

                log.info("Creating restaurant: {}", data.name());

            } else {

                restaurantsReused++;

                log.info("Reusing restaurant: {}", data.name());
            }

            // 2. Apply restaurant data.
            restaurant.setRestaurantName(data.name());
            restaurant.setAddress(data.address());
            restaurant.setPhone(data.phone());
            restaurant.setState(data.state());
            restaurant.setCity(data.city());
            restaurant.setCountry(data.country());
            restaurant.setPincode(data.pincode());
            restaurant.setImage(data.image());
            restaurant.setOpeningTime(
                    LocalTime.parse(data.openingTime())
            );
            restaurant.setClosingTime(
                    LocalTime.parse(data.closingTime())
            );

            Set<DayOfWeek> openDays = data.openDays()
                    .stream()
                    .map(day -> DayOfWeek.valueOf(
                            day.toUpperCase()
                    ))
                    .collect(Collectors.toSet());

            restaurant.setOpenDays(openDays);
            restaurant.setApproved(true);
            restaurant.setSeeded(true);

            restaurant = restaurantRepository.save(restaurant);

            // 3. Create owner mapping only if it doesn't exist.
            boolean mappingExists =
                    userRestaurantRepository
                            .existsByUserAndRestaurant(owner, restaurant);

            if (!mappingExists) {

                UserRestaurant mapping = new UserRestaurant();

                mapping.setUser(owner);
                mapping.setRestaurant(restaurant);
                mapping.setRole(ownerRole);

                userRestaurantRepository.save(mapping);

                log.info(
                        "Owner mapping created for: {}",
                        data.name()
                );
            }

            // 4. Insert or update menu items.
            for (var menuData : data.menu()) {

                MenuItem menuItem = menuItemRepository
                        .findByRestaurantAndMenuName(
                                restaurant,
                                menuData.name()
                        )
                        .orElse(null);

                boolean isNew = (menuItem == null);

                if (isNew) {
                    menuItem = new MenuItem();
                    menuItem.setRestaurant(restaurant);
                    menuItemsCreated++;
                } else {
                    menuItemsUpdated++;
                }

                menuItem.setMenuName(menuData.name());
                menuItem.setDescription(menuData.description());
                menuItem.setCategory(menuData.category());
                menuItem.setPrice(menuData.price());
                menuItem.setAvailable(true);

                // Update image only when seed data provides one.
                // This prevents an existing image from being replaced by null.
                if (menuData.image() != null
                        && !menuData.image().isBlank()) {
                    menuItem.setImage(menuData.image());
                }

                menuItemRepository.save(menuItem);
            }
        }

        log.info("========== SEEDING SUMMARY ==========");
        log.info("Owner: {}", owner.getEmail());
        log.info("Restaurants created: {}", restaurantsCreated);
        log.info("Restaurants reused: {}", restaurantsReused);
        log.info("Menu items created: {}", menuItemsCreated);
        log.info("Menu items processed for update: {}", menuItemsUpdated);
        log.info("RMS SEEDER COMPLETED SUCCESSFULLY!");
    }
}
