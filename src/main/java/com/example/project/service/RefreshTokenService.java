package com.example.project.service;

import com.example.project.model.RefreshToken;
import com.example.project.model.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

    void revokeToken(RefreshToken refreshToken);

    void revokeAllUserTokens(User user);
}
