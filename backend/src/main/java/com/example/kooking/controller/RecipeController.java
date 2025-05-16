package com.example.kooking.controller;

import com.example.kooking.dto.*;
import com.example.kooking.enums.*;
import com.example.kooking.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<PageResponse<RecipeCardDto>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        if (!Arrays.asList("name", "difficulty", "cookingTime", "rating").contains(sortBy)) {
            sortBy = "name";
        }
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Page<RecipeCardDto> recipes = recipeService.getRecipes(page, size, search, sortBy, direction);
        return ResponseEntity.ok(PageResponse.from(recipes));
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<PageResponse<RecipeCardDto>> advancedSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<UUID> includeIngredients,
            @RequestParam(required = false) List<UUID> excludeIngredients,
            @RequestParam(required = false) Integer maxCookingTime,
            @RequestParam(required = false) List<CuisineType> cuisines,
            @RequestParam(required = false) List<MealType> mealTypes,
            @RequestParam(required = false) List<DietType> dietTypes,
            @RequestParam(required = false) List<CookingMethod> cookingMethods,
            @RequestParam(required = false) List<DifficultyLevel> difficulties,
            @RequestParam(required = false, defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        List<String> allowedSortFields = List.of("name", "difficulty", "cuisine", "mealType",
                "dietType", "cookingMethod", "rating", "popularity", "cookingTime");

        if (!allowedSortFields.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field: " + sortBy);
        }

        AdvancedRecipeFilterCriteria criteria = AdvancedRecipeFilterCriteria.builder()
                .search(search)
                .includeIngredients(includeIngredients)
                .excludeIngredients(excludeIngredients)
                .maxCookingTime(maxCookingTime)
                .cuisines(cuisines)
                .mealTypes(mealTypes)
                .dietTypes(dietTypes)
                .cookingMethods(cookingMethods)
                .difficulties(difficulties)
                .build();

        Page<RecipeCardDto> recipePage = recipeService.advancedSearch(page, size, criteria, sortBy, sortDirection);

        return ResponseEntity.ok(PageResponse.from(recipePage));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<RecipeCardDto>> getTrendingRecipes() {
        return ResponseEntity.ok(recipeService.getTrendingRecipes());
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<RecipeCardDto>> getRecommendedRecipes() {
        return ResponseEntity.ok(recipeService.getRecommendedRecipes());
    }

    @GetMapping("/seasonal")
    public ResponseEntity<List<RecipeCardDto>> getSeasonalRecipes() {
        return ResponseEntity.ok(recipeService.getSeasonalRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailDto> getRecipeById(@PathVariable UUID id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @GetMapping("/{id}/brief")
    public ResponseEntity<RecipeBriefDto> getRecipeBrief(@PathVariable UUID id) {
        return ResponseEntity.ok(recipeService.getRecipeBrief(id));
    }

    @GetMapping("/user")
    public ResponseEntity<PageResponse<RecipeCardDto>> getUserRecipes(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false, defaultValue = "newest") String sort) {
        String userEmail = authentication.getName();
        Page<RecipeCardDto> recipePage = recipeService.getUserRecipes(page, size, search, cuisine, mealType, sort, userEmail);
        return ResponseEntity.ok(PageResponse.from(recipePage));
    }

    @PostMapping
    public ResponseEntity<RecipeDetailDto> createRecipe(
            @Valid @RequestBody CreateRecipeDto createRecipeDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeService.createRecipe(createRecipeDto, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDetailDto> updateRecipe(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRecipeDto updateRecipeDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(recipeService.updateRecipe(id, updateRecipeDto, userEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteRecipe(@PathVariable UUID id, Authentication authentication) {
        String userEmail = authentication.getName();
        recipeService.favoriteRecipe(id, userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unfavoriteRecipe(@PathVariable UUID id, Authentication authentication) {
        String userEmail = authentication.getName();
        recipeService.unfavoriteRecipe(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}

