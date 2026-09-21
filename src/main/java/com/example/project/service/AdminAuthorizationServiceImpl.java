package com.example.project.service;

import com.example.project.exception.ForbiddenException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.RoleType;
import com.example.project.model.User;
import com.example.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthorizationServiceImpl implements AdminAuthorizationService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isAdmin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId
                        ));

        return user.getRole() != null && user.getRole().getRoleName() == RoleType.ROLE_ADMIN;
    }

    @Override
    public void checkAdmin(Long userId) {

        if (!isAdmin(userId)) {
            throw new ForbiddenException("Only administrator can access this resource");
        }
    }
}
