package com.example.project.service;

import com.example.project.dto.request.PermissionRequest;
import com.example.project.dto.response.PermissionResponse;
import com.example.project.exception.APIException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.Permission;
import com.example.project.repositories.PermissionRepository;
import com.example.project.repositories.RolePermissionRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {

        String permissionName = request.getPermissionName();

        if (permissionRepository.existsByPermissionName(permissionName)) {
            throw new APIException("Permission already exists: " + permissionName);
        }

        Permission permission = new Permission();

        permission.setPermissionName(permissionName);

        Permission savedPermission = permissionRepository.save(permission);

        return modelMapper.map(savedPermission, PermissionResponse.class);
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {

        return permissionRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PermissionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermissionById(Long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        return modelMapper.map(permission, PermissionResponse.class);
    }

    @Override
    public PermissionResponse updatePermission(Long permissionId, PermissionRequest request) {

        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Permission not found with id: " + permissionId));

        String newPermissionName = request.getPermissionName().trim()
                .toUpperCase();
        permissionRepository.findByPermissionName(newPermissionName)
                .ifPresent(existingPermission -> {
                    if (!existingPermission.getId().equals(permissionId)) {
                        throw new APIException("Permission already exists: " + newPermissionName);
                    }
                });

        permission.setPermissionName(newPermissionName);

        Permission updatedPermission = permissionRepository.save(permission);

        return modelMapper.map(updatedPermission, PermissionResponse.class);
    }

    @Override
    public String deletePermission(Long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Permission not found with id: " + permissionId));

        if (rolePermissionRepository.existsByPermission(permission)) {
            throw new APIException("Permission cannot be deleted because "
                            + "it is assigned to one or more roles"
            );
        }

        permissionRepository.delete(permission);
        return "permission deleted successfully ";
    }

}
