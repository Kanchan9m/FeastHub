package com.example.project.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank(message = "Menu name is required")
    @Size(max = 100, message = "Menu name cannot exceed 100 characters")
    private String menuName;

    private String description;

    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Price must be greater than zero")
    private BigDecimal price;

    private String image;

    private Boolean available;
}
