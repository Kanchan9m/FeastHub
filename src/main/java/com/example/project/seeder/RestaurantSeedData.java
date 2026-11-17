package com.example.project.seeder;

import java.math.BigDecimal;
import java.util.List;

public class RestaurantSeedData {

    public record MenuData(
            String name,
            String description,
            String category,
            BigDecimal price,
            String image
    ) {}

    public record RestaurantData(
            String name,
            String address,
            String phone,
            String state,
            String city,
            String country,
            String pincode,
            String image,
            String openingTime,
            String closingTime,
            List<String> openDays,
            List<MenuData> menu
    ) {}

    public static List<RestaurantData> getRestaurants() {

        return List.of(new RestaurantData(
                        "Bukhara",
                        "Main Market, Delhi",
                        "9876543210",
                        "Delhi",
                        "New Delhi",
                        "India",
                        "110001",
                        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
                        "08:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(new MenuData(
                                        "Dal Bukhara",
                                        "Signature North Indian dish",
                                        "Main Course",
                                        new BigDecimal("403.00"),
                                        "https://images.unsplash.com/photo-1668236543090-82eba5ee5976"),
                                new MenuData(
                                        "Paneer Tikka",
                                        "Grilled paneer with spices",
                                        "Starters",
                                        new BigDecimal("250.00"),
                                        null
                                ),
                                new MenuData(
                                        "Butter Naan",
                                        "Soft Indian bread",
                                        "Breads",
                                        new BigDecimal("60.00"),
                                        null
                                )
                        )
                ),

                new RestaurantData("Pizza Corner",
                        "Sector 18, Noida",
                        "9876543211",
                        "Uttar Pradesh",
                        "Noida",
                        "India",
                        "201301",
                        "https://images.unsplash.com/photo-1513104890138-7c749659a591",
                        "10:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Margherita Pizza",
                                        "Classic cheese pizza",
                                        "Pizza",
                                        new BigDecimal("299.00"),
                                        null
                                ),
                                new MenuData(
                                        "Farmhouse Pizza",
                                        "Pizza with fresh vegetables",
                                        "Pizza",
                                        new BigDecimal("399.00"),
                                        null
                                )
                        )
                ),

                new RestaurantData(
                        "Burger House",
                        "Cyber Hub, Gurgaon",
                        "9876543212",
                        "Haryana",
                        "Gurgaon",
                        "India",
                        "122002",
                        "https://images.unsplash.com/photo-1550547660-d9450f859349",
                        "11:00",
                        "23:30",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Classic Burger",
                                        "Burger with fresh vegetables",
                                        "Burgers",
                                        new BigDecimal("149.00"),
                                        null
                                ),
                                new MenuData(
                                        "Cheese Burger",
                                        "Burger with extra cheese",
                                        "Burgers",
                                        new BigDecimal("199.00"),
                                        null
                                )
                        )
                ),
                new RestaurantData(
                        "Spice Villa",
                        "Sector 18, Noida",
                        "9876543210",
                        "Uttar Pradesh",
                        "Noida",
                        "India",
                        "201301",
                        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
                        "10:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Butter Chicken",
                                        "Creamy chicken curry with Indian spices",
                                        "Main Course",
                                        new BigDecimal("349.00"),
                                        "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398"
                                ),
                                new MenuData(
                                        "Paneer Tikka",
                                        "Grilled cottage cheese with spices",
                                        "Starters",
                                        new BigDecimal("249.00"),
                                        "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8"
                                )
                        )
                ),
                new RestaurantData(
                        "Royal Thali",
                        "MI Road, Jaipur",
                        "9876543211",
                        "Rajasthan",
                        "Jaipur",
                        "India",
                        "302001",
                        "https://images.unsplash.com/photo-1546833999-b9f581a1996d",
                        "09:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Rajasthani Thali",
                                        "Traditional Rajasthani platter with dal, roti and vegetables",
                                        "Main Course",
                                        new BigDecimal("299.00"),
                                        "https://images.unsplash.com/photo-1547592180-85f173990554"
                                ),
                                new MenuData(
                                        "Dal Baati Churma",
                                        "Traditional baked wheat balls served with dal and churma",
                                        "Main Course",
                                        new BigDecimal("249.00"),
                                        "https://images.unsplash.com/photo-1601050690597-df0568f70950"
                                )
                        )
                ),

                new RestaurantData(
                        "The Wok Express",
                        "Indiranagar, Bengaluru",
                        "9876543212",
                        "Karnataka",
                        "Bengaluru",
                        "India",
                        "560038",
                        "https://images.unsplash.com/photo-1552566626-52f8b828add9",
                        "11:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY"),
                        List.of(
                                new MenuData(
                                        "Hakka Noodles",
                                        "Stir-fried noodles with vegetables and Chinese sauces",
                                        "Main Course",
                                        new BigDecimal("199.00"),
                                        "https://images.unsplash.com/photo-1585032226651-759b368d7246"
                                ),
                                new MenuData(
                                        "Veg Manchurian",
                                        "Crispy vegetable balls tossed in spicy Manchurian sauce",
                                        "Starters",
                                        new BigDecimal("179.00"),
                                        "https://images.unsplash.com/photo-1512058564366-18510be2db19"
                                )
                        )
                ),

                new RestaurantData(
                        "Coastal Bites",
                        "Fort Kochi, Kochi",
                        "9876543213",
                        "Kerala",
                        "Kochi",
                        "India",
                        "682001",
                        "https://images.unsplash.com/photo-1554118811-1e0d58224f24?q=80&w=1500&auto=format&fit=crop",
                        "10:00",
                        "22:30",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Kerala Fish Curry",
                                        "Traditional Kerala-style fish curry with coconut and spices",
                                        "Main Course",
                                        new BigDecimal("299.00"),
                                        "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2"
                                ),
                                new MenuData(
                                        "Prawn Masala",
                                        "Fresh prawns cooked in a spicy coastal masala",
                                        "Main Course",
                                        new BigDecimal("349.00"),
                                        "https://images.unsplash.com/photo-1625944525533-473f1a3d54e7"
                                )
                        )
                ),

                new RestaurantData(
                        "Sweet Cravings",
                        "Koregaon Park, Pune",
                        "9876543214",
                        "Maharashtra",
                        "Pune",
                        "India",
                        "411001",
                        "https://images.unsplash.com/photo-1555396273-367ea4eb4db5",
                        "10:00",
                        "23:00",
                        List.of("MONDAY", "TUESDAY", "WEDNESDAY",
                                "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
                        List.of(
                                new MenuData(
                                        "Chocolate Cake",
                                        "Rich chocolate cake with creamy chocolate frosting",
                                        "Desserts",
                                        new BigDecimal("199.00"),
                                        "https://images.unsplash.com/photo-1578985545062-69928b1d9587"
                                ),
                                new MenuData(
                                        "Chocolate Brownie",
                                        "Warm chocolate brownie with a fudgy center",
                                        "Desserts",
                                        new BigDecimal("149.00"),
                                        "https://images.unsplash.com/photo-1606313564200-e75d5e30476c"
                                )
                        )
                )
        );
    }
}