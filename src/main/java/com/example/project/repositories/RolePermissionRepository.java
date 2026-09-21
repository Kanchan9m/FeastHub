package com.example.project.repositories;

import com.example.project.model.Permission;
import com.example.project.model.Role;
import com.example.project.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);

    boolean existsByRoleAndPermission(Role role, Permission permission);

    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);

    boolean existsByPermission(Permission permission);

    boolean existsByRole(Role role);

    void deleteByRoleAndPermission(Role role, Permission permission);
}
