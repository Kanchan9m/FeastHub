package com.example.project.service;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);

    List<PermissionResponse> getAllPermissions();

    PermissionResponse getPermissionById(Long permissionId);

    PermissionResponse updatePermission(Long permissionId, PermissionRequest request);

    String deletePermission(Long permissionId);
}
