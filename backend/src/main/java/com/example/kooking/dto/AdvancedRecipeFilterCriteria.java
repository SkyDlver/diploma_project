package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedRecipeFilterCriteria {
    private String search;
    private List<UUID> includeIngredients;
    private List<UUID> excludeIngredients;
    private Integer maxCookingTime;
    private List<CuisineType> cuisines;
    private List<MealType> mealTypes;
    private List<DietType> dietTypes;
    private List<CookingMethod> cookingMethods;
    private List<DifficultyLevel> difficulties;
}