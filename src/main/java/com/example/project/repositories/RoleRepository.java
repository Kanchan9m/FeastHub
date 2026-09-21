package com.example.project.repositories;

import com.example.project.model.Role;
import com.example.project.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleType rolename);
    boolean existsByRoleName(RoleType roleName);
}
