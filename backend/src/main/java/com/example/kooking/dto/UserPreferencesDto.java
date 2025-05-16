package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPreferencesDto {
    private Set<CuisineType> preferredCuisine;
    private Set<MealType> preferredMealTypes;
    private Set<DietType> dietaryRestrictions;
    private Set<CookingMethod> preferredCookingMethods;
    private DifficultyLevel preferredDifficulty;
}
