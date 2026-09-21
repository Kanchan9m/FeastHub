package com.example.project.service;

import com.example.project.dto.response.RolePermissionResponse;
import com.example.project.exception.APIException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.model.RolePermission;
import com.example.project.repositories.PermissionRepository;
import com.example.project.repositories.RolePermissionRepository;
import com.example.project.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public RolePermissionResponse assignPermissionToRole(Long roleId, Long permissionId) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId
                        ));

        if (rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
            throw new APIException("Permission is already assigned to this role");
        }
        RolePermission rolePermission = new RolePermission();

        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        RolePermission saved = rolePermissionRepository.save(rolePermission);

        return modelMapper.map(saved, RolePermissionResponse.class);
    }

    @Override
    public String removePermissionFromRole(Long roleId, Long permissionId) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId
                        ));

        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        RolePermission rolePermission = rolePermissionRepository.findByRoleAndPermission(role, permission)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission is not assigned to this role"));

        rolePermissionRepository.delete(rolePermission);
        return "permission delete successfully from role with permissionId: "+ permissionId;
    }

    @Override
    public List<RolePermissionResponse> getPermissionsByRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        return rolePermissionRepository.findByRole(role)
                .stream()
                .map(p -> modelMapper.map(p, RolePermissionResponse.class))
                .collect(Collectors.toList());
    }
}
