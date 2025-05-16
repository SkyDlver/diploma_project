package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCardDto {
    private UUID id;
    private String name;
    private String description;
    private CuisineType cuisine;
    private MealType mealType;
    private int cookingTime;
    private DietType dietType;
    private CookingMethod cookingMethod;
    private DifficultyLevel difficulty;
    private String imageUrl;
    private BigDecimal rating;
    private int popularity;
}

