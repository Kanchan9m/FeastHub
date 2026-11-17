package com.example.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeeRequest {

    @NotBlank
    @Size(max = 100)
    private String employeeName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    private String phone;

    @NotBlank
    private String role;
}
