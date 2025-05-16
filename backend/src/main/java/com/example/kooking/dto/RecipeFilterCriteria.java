package com.example.kooking.dto;

import com.example.kooking.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class RecipeFilterCriteria {
    private String search;
    private CuisineType cuisine;
    private MealType mealType;
    private DietType dietType;
    private CookingMethod cookingMethod;
    private DifficultyLevel difficulty;
}
