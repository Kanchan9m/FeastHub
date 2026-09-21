package com.example.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotNull(message = "User Id is required")
    private Long userId;

    @NotNull(message = "Restaurant Id is required")
    private Long restaurantId;

    @NotNull(message = "Role Id is required")
    private Long roleId;
}
