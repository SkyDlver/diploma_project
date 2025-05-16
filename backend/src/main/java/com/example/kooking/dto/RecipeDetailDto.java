package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailDto {
    private UUID id;
    private String name;
    private String description;
    private CuisineType cuisine;
    private MealType mealType;
    private int cookingTime;
    private DietType dietType;
    private CookingMethod cookingMethod;
    private DifficultyLevel difficulty;
    private List<RecipeIngredientDto> ingredients;
    private String instructions;
    private String imageUrl;
    private BigDecimal rating;
    private int popularity;
    private UserProfileDto author;
    private boolean isFavorite;
}