package com.example.project.service;

public interface AdminAuthorizationService {
    void checkAdmin(Long userId);

    boolean isAdmin(Long userId);
}
