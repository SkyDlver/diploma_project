package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeBriefDto {
    private UUID id;
    private String name;
    private String description;
    private CuisineType cuisine;
    private MealType mealType;
    private int cookingTime;
    private String imageUrl;
    private BigDecimal rating;
    private UserProfileDto author;
}