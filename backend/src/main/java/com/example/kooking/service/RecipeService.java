package com.example.kooking.service;

import com.example.kooking.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Page<RecipeCardDto> getRecipes(int page, int size, String search, String sortBy, Sort.Direction direction);

    Page<RecipeCardDto> advancedSearch(int page, int size, AdvancedRecipeFilterCriteria criteria, String sortBy, String sortDirection);

    void favoriteRecipe(UUID id, String userEmail);

    void unfavoriteRecipe(UUID id, String userEmail);

    List<RecipeCardDto> getTrendingRecipes();

    List<RecipeCardDto> getSeasonalRecipes();

    List<RecipeCardDto> getRecommendedRecipes();

    RecipeDetailDto getRecipeById(UUID recipeId);

    RecipeBriefDto getRecipeBrief(UUID id);

    void deleteRecipe(UUID id);

    RecipeDetailDto updateRecipe(UUID id, @Valid UpdateRecipeDto updateRecipeDto, String userEmail);

    RecipeDetailDto createRecipe(@Valid CreateRecipeDto createRecipeDto, String userEmail);

    Page<RecipeCardDto> getUserRecipes(int page, int size, String search, String cuisine, String mealType, String sort, String userEmail);
}