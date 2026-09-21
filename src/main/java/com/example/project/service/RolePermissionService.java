package com.example.project.service;

import com.example.project.dto.response.RolePermissionResponse;

import java.util.List;

public interface RolePermissionService {

    RolePermissionResponse assignPermissionToRole(Long roleId, Long permissionId);

    String removePermissionFromRole(Long roleId, Long permissionId);

    List<RolePermissionResponse> getPermissionsByRole(Long roleId);
}
