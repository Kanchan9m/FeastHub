package com.example.project.service;

import com.example.project.dto.request.RoleDTO;
import com.example.project.dto.response.RoleResponse;
import com.example.project.exception.APIException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.Role;
import com.example.project.model.RoleType;
import com.example.project.repositories.RolePermissionRepository;
import com.example.project.repositories.RoleRepository;
import com.example.project.repositories.UserRestaurantRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRestaurantRepository userRestaurantRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public RoleResponse createRole(RoleDTO request) {

        RoleType roleType = request.getRoleName();

        if (roleRepository.existsByRoleName(roleType)) {
            throw new APIException("Role already exists: " + roleType);
        }

        Role role = new Role();

        role.setRoleName(roleType);

        Role savedRole =
                roleRepository.save(role);

        return modelMapper.map(savedRole, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, RoleResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        return modelMapper.map(role, RoleResponse.class);
    }

    @Override
    public RoleResponse updateRole(Long roleId, RoleDTO request) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        RoleType newRoleType = request.getRoleName();

        roleRepository.findByRoleName(newRoleType).ifPresent(existingRole -> {

                    if (!existingRole.getId().equals(roleId)) {
                        throw new APIException("Role already exists: " + newRoleType);
                    }
                });
        role.setRoleName(newRoleType);

        Role updatedRole = roleRepository.save(role);

        return modelMapper.map(updatedRole, RoleResponse.class);
    }

    @Override
    public void deleteRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        if (userRestaurantRepository.existsByRole(role)) {
            throw new APIException("Role cannot be deleted because " + "it is assigned to one or more users"
            );
        }

        if (rolePermissionRepository.existsByRole(role)) {
            throw new APIException("Role cannot be deleted because " + "permissions are still assigned to it");
        }
        roleRepository.delete(role);
    }
}
