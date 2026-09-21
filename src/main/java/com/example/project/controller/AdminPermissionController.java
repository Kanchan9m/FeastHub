package com.example.project.controller;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.security.UserDetailsImpl;
import com.example.project.service.AdminAuthorizationService;
import com.example.project.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rms")
public class AdminPermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AdminAuthorizationService adminAuthorizationService;

    @PostMapping("/admin/permissions")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody PermissionRequest request,
                                                               Authentication authentication) {
        checkAdmin(authentication);
        PermissionResponse response = permissionService.createPermission(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/permissions")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions(Authentication authentication) {
        checkAdmin(authentication);
        List<PermissionResponse> permissions = permissionService.getAllPermissions();

        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/admin/permissions/{permissionId}")
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Long permissionId,
                                                                Authentication authentication) {
        checkAdmin(authentication);
        PermissionResponse response =
                permissionService.getPermissionById(
                        permissionId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/permissions/{permissionId}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable Long permissionId,
                                                               @Valid @RequestBody PermissionRequest request,
                                                               Authentication authentication) {
        checkAdmin(authentication);
        PermissionResponse response = permissionService.updatePermission(permissionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/permissions/{permissionId}")
    public ResponseEntity<String> deletePermission(@PathVariable Long permissionId, Authentication authentication) {

        checkAdmin(authentication);
        String status = permissionService.deletePermission(permissionId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    private void checkAdmin(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        adminAuthorizationService.checkAdmin(userDetails.getId()
        );
    }
}
