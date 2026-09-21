package com.example.project.service;

import com.example.project.dto.request.RoleDTO;
import com.example.project.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleDTO request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long roleId);

    RoleResponse updateRole(
            Long roleId,
            RoleDTO request);

    void deleteRole(Long roleId);
}
