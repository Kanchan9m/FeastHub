package com.example.project.controller;

import com.example.project.dto.response.RolePermissionResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.AdminAuthorizationService;
import com.example.project.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rms")
public class AdminRolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private AdminAuthorizationService adminAuthorizationService;

    @PostMapping("/admin/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RolePermissionResponse> assignPermission(@PathVariable Long roleId, @PathVariable Long permissionId,
                                                                   Authentication authentication) {

        checkAdmin(authentication);

        RolePermissionResponse response = rolePermissionService.assignPermissionToRole(roleId, permissionId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/roles/{roleId}/permissions")
    public ResponseEntity<List<RolePermissionResponse>> getPermissionsByRole(@PathVariable Long roleId, Authentication authentication) {

        checkAdmin(authentication);

        return ResponseEntity.ok(rolePermissionService.getPermissionsByRole(roleId));
    }


    @DeleteMapping("/admin/roles/{roleId}/permissions/{permissionId}")
    public ResponseEntity<String> removePermission(@PathVariable Long roleId, @PathVariable Long permissionId,
                                                 Authentication authentication) {

        checkAdmin(authentication);
        String status = rolePermissionService.removePermissionFromRole(roleId, permissionId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    private void checkAdmin(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        adminAuthorizationService.checkAdmin(userDetails.getId());
    }
}
