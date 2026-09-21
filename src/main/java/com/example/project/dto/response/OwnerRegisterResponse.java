package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRegisterResponse {
    private Long id;

    private String username;

    private String email;

    private String phone;

    private Boolean emailVerified;

    private Boolean active;

    private String message;
}
