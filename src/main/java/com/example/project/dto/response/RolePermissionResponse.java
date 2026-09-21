package com.example.project.dto.response;

import com.example.project.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    private Long id;

    private Long roleId;

    private RoleType roleName;

    private Long permissionId;

    private String permissionName;
}
