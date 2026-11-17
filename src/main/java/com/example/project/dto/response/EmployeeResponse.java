package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long employeeId;
    private Long assignmentId;
    private String employeeName;
    private String email;
    private String phone;
    private String role;
}
