package com.example.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private Long id;
    private String jwtToken;

    private String refreshToken;
    private String username;
    private String email;
    private String role;

    public JwtResponse(Long id, String jwtToken, String refreshToken, String username, String email) {
        this.id = id;
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.email = email;
    }
}
