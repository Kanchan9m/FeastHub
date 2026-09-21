package com.example.project.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Table(name = "email_verification")
public class EmailVerificationOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Instant expiresAt;


    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false)
    private Integer attemptCount = 0;

    @Column(nullable = false)
    private Instant createdAt;
}
