package com.example.kooking.dto;

import com.example.kooking.enums.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecipeDto {
    @NotBlank(message = "Recipe name is required")
    private String name;

    private String description;

    @NotNull(message = "Cuisine type is required")
    private CuisineType cuisine;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @Min(value = 1, message = "Cooking time must be at least 1 minute")
    private int cookingTime;

    private DietType dietType;
    private CookingMethod cookingMethod;
    private DifficultyLevel difficulty;

    private List<RecipeIngredientDto> ingredients;

    @NotBlank(message = "Instructions are required")
    private String instructions;

    private String imageUrl;
}