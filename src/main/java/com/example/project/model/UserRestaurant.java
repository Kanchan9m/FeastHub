package com.example.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurants restaurant;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

}
