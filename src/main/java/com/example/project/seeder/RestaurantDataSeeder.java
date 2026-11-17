//
//package com.example.project.seeder;
//
//import com.example.project.model.Restaurants;
//import com.example.project.model.MenuItem;
//import com.example.project.model.User;
//import com.example.project.model.UserRestaurant;
//
//import com.example.project.repositories.RestaurantRepository;
//import com.example.project.repositories.MenuItemRepository;
//import com.example.project.repositories.UserRepository;
//import com.example.project.repositories.UserRestaurantRepository;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.DayOfWeek;
//import java.time.LocalTime;
//import java.util.EnumSet;
//
//@Component
//@Profile("dev")
//public class RestaurantDataSeeder implements CommandLineRunner {
//
//    private static final String DEMO_OWNER_EMAIL = "nina@gmail.com";
//
//    private final RestaurantRepository restaurantsRepository;
//    private final MenuItemRepository menuItemRepository;
//    private final UserRepository userRepository;
//    private final UserRestaurantRepository userRestaurantRepository;
//
//    public RestaurantDataSeeder(
//            RestaurantRepository restaurantsRepository,
//            MenuItemRepository menuItemRepository,
//            UserRepository userRepository,
//            UserRestaurantRepository userRestaurantRepository) {
//
//        this.restaurantsRepository = restaurantsRepository;
//        this.menuItemRepository = menuItemRepository;
//        this.userRepository = userRepository;
//        this.userRestaurantRepository = userRestaurantRepository;
//    }
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//
//        // Step 1: Prevent duplicate sample data
//        if (restaurantsRepository.existsBySeededTrue()) {
//            System.out.println(
//                    "Sample data already exists. Skipping seeder."
//            );
//            return;
//        }
//
//        System.out.println("Starting restaurant seeder...");
//
//        // Step 2: Find the existing demo owner
//        User owner = userRepository.findByEmail(DEMO_OWNER_EMAIL)
//                .orElseThrow(() -> new IllegalStateException(
//                        "Demo owner not found. Please create the owner account first: "
//                                + DEMO_OWNER_EMAIL
//                ));
//
//        // The owner must have a role assigned
//        if (owner.getRole() == null) {
//            throw new IllegalStateException(
//                    "Demo owner does not have a role assigned."
//            );
//        }
//
//        // Step 3: Create Noida restaurant
//        createRestaurant(
//                owner,
//                "North Indian Kitchen",
//                "Sector 18, Noida",
//                "Noida",
//                "Uttar Pradesh",
//                "201301",
//                "9876543210",
//                28.5708,
//                77.3261,
//                "/images/restaurants/north-indian-kitchen.jpg",
//                LocalTime.of(10, 0),
//                LocalTime.of(23, 0),
//                EnumSet.allOf(DayOfWeek.class),
//                new String[]{
//                        "Paneer Tikka",
//                        "Dal Makhani",
//                        "Butter Naan"
//                },
//                new String[]{
//                        "/images/menu/paneer-tikka.jpg",
//                        "/images/menu/dal-makhani.jpg",
//                        "/images/menu/butter-naan.jpg"
//                }
//        );
//
//        // Step 4: Create Delhi restaurant
//        createRestaurant(
//                owner,
//                "Delhi Food Corner",
//                "Connaught Place, Delhi",
//                "Delhi",
//                "Delhi",
//                "110001",
//                "9876543211",
//                28.6315,
//                77.2167,
//                "/images/restaurants/delhi-food-corner.jpg",
//                LocalTime.of(9, 0),
//                LocalTime.of(22, 0),
//                EnumSet.range(
//                        DayOfWeek.MONDAY,
//                        DayOfWeek.SATURDAY
//                ),
//                new String[]{
//                        "Chole Bhature",
//                        "Rajma Chawal",
//                        "Aloo Paratha"
//                },
//                new String[]{
//                        "/images/menu/chole-bhature.jpg",
//                        "/images/menu/rajma-chawal.jpg",
//                        "/images/menu/aloo-paratha.jpg"
//                }
//        );
//
//        System.out.println("Restaurant seeding completed!");
//    }
//
//    private void createRestaurant(
//            User owner,
//            String name,
//            String address,
//            String city,
//            String state,
//            String pincode,
//            String phone,
//            double latitude,
//            double longitude,
//            String image,
//            LocalTime openingTime,
//            LocalTime closingTime,
//            EnumSet<DayOfWeek> openDays,
//            String[] menuNames,
//            String[] menuImages) {
//
//        // Step 5: Validate menu data
//        if (menuNames.length != menuImages.length) {
//            throw new IllegalArgumentException(
//                    "Menu names and images must have the same length for "
//                            + name
//            );
//        }
//
//        // Step 6: Create restaurant
//        Restaurants restaurant = new Restaurants();
//
//        restaurant.setRestaurantName(name);
//        restaurant.setAddress(address);
//        restaurant.setCity(city);
//        restaurant.setState(state);
//        restaurant.setCountry("India");
//        restaurant.setPhone(phone);
//        restaurant.setPincode(pincode);
//
//        restaurant.setLatitude(latitude);
//        restaurant.setLongitude(longitude);
//
//        restaurant.setImage(image);
//
//        restaurant.setOpeningTime(openingTime);
//        restaurant.setClosingTime(closingTime);
//        restaurant.setOpenDays(openDays);
//
//        restaurant.setApproved(true);
//        restaurant.setSeeded(true);
//
//        // Step 7: Save restaurant
//        Restaurants savedRestaurant =
//                restaurantsRepository.save(restaurant);
//
//        System.out.println("Created restaurant: " + name);
//
//        // Step 8: Associate restaurant with owner
//        if (!userRestaurantRepository.existsByUserAndRestaurant(
//                owner, savedRestaurant)) {
//
//            UserRestaurant userRestaurant = new UserRestaurant();
//
//            userRestaurant.setUser(owner);
//            userRestaurant.setRestaurant(savedRestaurant);
//
//            // Use the owner's existing role
//            userRestaurant.setRole(owner.getRole());
//
//            userRestaurantRepository.save(userRestaurant);
//
//            System.out.println(
//                    "Associated restaurant with owner: "
//                            + owner.getEmail()
//            );
//        }
//
//        // Step 9: Create menu items
//        for (int i = 0; i < menuNames.length; i++) {
//
//            MenuItem item = new MenuItem();
//
//            item.setMenuName(menuNames[i]);
//            item.setDescription(
//                    "Freshly prepared sample menu item"
//            );
//            item.setCategory("Main Course");
//            item.setImage(menuImages[i]);
//
//            item.setPrice(
//                    BigDecimal.valueOf(150L + (i * 50L))
//            );
//
//            item.setAvailable(true);
//
//            // Associate menu item with its restaurant
//            item.setRestaurant(savedRestaurant);
//
//            menuItemRepository.save(item);
//        }
//
//        System.out.println(
//                "Created " + menuNames.length
//                        + " menu items for " + name
//        );
//    }
//}
