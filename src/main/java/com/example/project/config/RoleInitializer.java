package com.example.project.config;

import com.example.project.model.Role;
import com.example.project.model.RoleType;
import com.example.project.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleInitializer {

    @Bean
    CommandLineRunner initializeRoles(RoleRepository roleRepository) {

        return args -> {

            for (RoleType roleType : RoleType.values()) {

                if (roleRepository.findByRoleName(roleType).isEmpty()) {

                    Role role = new Role();
                    role.setRoleName(roleType);

                    roleRepository.save(role);
                }
            }
        };
    }
}
