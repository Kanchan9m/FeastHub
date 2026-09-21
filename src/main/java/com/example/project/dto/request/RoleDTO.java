package com.example.project.dto.request;

import com.example.project.model.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleDTO {
    @NotNull(message = "Role name is required")
    private RoleType roleName;
}
