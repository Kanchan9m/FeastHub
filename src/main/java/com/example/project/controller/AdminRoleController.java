package com.example.project.controller;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.request.RoleDTO;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.dto.response.RoleResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.AdminAuthorizationService;
import com.example.project.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rms")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminAuthorizationService adminAuthorizationService;

    @PostMapping("/admin/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleDTO request,
                                                               Authentication authentication) {
        checkAdmin(authentication);
        RoleResponse response = roleService.createRole(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private void checkAdmin(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        adminAuthorizationService.checkAdmin(userDetails.getId()
        );
    }
}
