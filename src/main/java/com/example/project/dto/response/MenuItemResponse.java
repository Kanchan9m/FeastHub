package com.example.project.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemResponse {
    private Long id;
    private String menuName;
    private String description;
    private String category;
    private BigDecimal price;
    private String image;
    private Boolean available;
    private Long restaurantId;
}
