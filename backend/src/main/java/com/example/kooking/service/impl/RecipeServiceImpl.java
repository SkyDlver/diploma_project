package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.CuisineType;
import com.example.kooking.enums.MealType;
import com.example.kooking.enums.Season;
import com.example.kooking.model.*;
import com.example.kooking.repository.FavoriteRecipeRepository;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.service.RecipeService;
import com.example.kooking.utils.RecipeMapper;
import com.example.kooking.utils.RecipeSpecification;
import com.example.kooking.utils.RecipeUtility;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.kooking.dto.RecipeCardDto;


import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final FavoriteRecipeRepository favoriteRecipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeUtility recipeUtility;

    @Override
    public Page<RecipeCardDto> getRecipes(int page, int size, String search, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Recipe> recipePage;
        if (search != null && !search.trim().isEmpty()) {
            recipePage = recipeRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            recipePage = recipeRepository.findAll(pageable);
        }

        return recipePage.map(recipeMapper::recipeToRecipeCardDto);
    }

    @Override
    public Page<RecipeCardDto> advancedSearch(int page, int size, AdvancedRecipeFilterCriteria criteria, String sortBy, String sortDirection) {
        Map<String, String> sortFieldMappings = Map.of(
                "name", "name",
                "difficulty", "difficulty",
                "cuisine", "cuisine",
                "mealType", "mealType",
                "dietType", "dietType",
                "cookingMethod", "cookingMethod",
                "rating", "rating",
                "popularity", "popularity",
                "cookingTime", "cookingTime"
        );

        String entitySortField = sortFieldMappings.getOrDefault(sortBy, "name");

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection.toUpperCase()), entitySortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Recipe> specification = RecipeSpecification.advancedFilterByCriteria(criteria);
        Page<Recipe> recipePage = recipeRepository.findAll(specification, pageable);

        return recipePage.map(recipeMapper::recipeToRecipeCardDto);
    }

    @Override
    @Transactional
    public void favoriteRecipe(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));
        if (favoriteRecipeRepository.existsByUserAndRecipe(user, recipe)) {
            throw new IllegalStateException("Recipe is already in favorites");
        }
        FavoriteRecipe favoriteRecipe = FavoriteRecipe.builder()
                .user(user)
                .recipe(recipe)
                .build();

        recipe.setPopularity(recipe.getPopularity() + 1);
        favoriteRecipeRepository.save(favoriteRecipe);
    }

    @Override
    @Transactional
    public void unfavoriteRecipe(UUID id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

        FavoriteRecipe favoriteRecipe = favoriteRecipeRepository.findByUserAndRecipe(user, recipe)
                .orElseThrow(() -> new IllegalStateException("Recipe is not in favorites"));

        favoriteRecipeRepository.delete(favoriteRecipe);
        if (recipe.getPopularity() > 0) {
            recipe.setPopularity(recipe.getPopularity() - 1);
            recipeRepository.save(recipe);
        }
    }

    @Override
    public List<RecipeCardDto> getTrendingRecipes() {
        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Recipe> trendingRecipes = recipeRepository.findAll(topTwenty);

        return trendingRecipes.getContent().stream()
                .map(recipeMapper::recipeToRecipeCardDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeCardDto> getSeasonalRecipes() {
        Season currentSeason = recipeUtility.getCurrentSeason();
        List<CuisineType> seasonalCuisines = recipeUtility.getSeasonalCuisines(currentSeason);

        Specification<Recipe> spec = (root, query, cb) ->
                root.get("cuisine").in(seasonalCuisines);

        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Recipe> seasonalRecipes = recipeRepository.findAll(spec, topTwenty);

        return seasonalRecipes.getContent().stream()
                .map(recipeMapper::recipeToRecipeCardDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeCardDto> getRecommendedRecipes() {
        MealType currentMealType = recipeUtility.getCurrentMealType();

        Specification<Recipe> spec = (root, query, cb) ->
                cb.equal(root.get("mealType"), currentMealType);

        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Recipe> recommendedRecipes = recipeRepository.findAll(spec, topTwenty);
        if (recommendedRecipes.getContent().size() < 20) {
            int remaining = 20 - recommendedRecipes.getContent().size();
            Pageable remainingRecipes = PageRequest.of(0, remaining, Sort.by(Sort.Direction.DESC, "popularity"));

            List<UUID> existingIds = recommendedRecipes.getContent().stream()
                    .map(Recipe::getId)
                    .collect(Collectors.toList());

            Specification<Recipe> remainingSpec = (root, query, cb) ->
                    !existingIds.isEmpty() ? root.get("id").in(existingIds).not() : null;

            Page<Recipe> additionalRecipes = recipeRepository.findAll(remainingSpec, remainingRecipes);
            List<Recipe> combined = new ArrayList<>(recommendedRecipes.getContent());
            combined.addAll(additionalRecipes.getContent());

            return combined.stream()
                    .map(recipeMapper::recipeToRecipeCardDto)
                    .collect(Collectors.toList());
        }

        return recommendedRecipes.getContent().stream()
                .map(recipeMapper::recipeToRecipeCardDto)
                .collect(Collectors.toList());
    }

    @Override
    public RecipeDetailDto getRecipeById(UUID recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        return recipeMapper.recipeToRecipeDetailDto(recipe);
    }

    public RecipeBriefDto getRecipeBrief(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        return recipeMapper.recipeToRecipeBriefDto(recipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        recipeRepository.delete(recipe);
    }

    @Override
    @Transactional
    public RecipeDetailDto updateRecipe(UUID id, @Valid UpdateRecipeDto updateRecipeDto, String userEmail) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!recipe.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this recipe");
        }

        recipe.setName(updateRecipeDto.getName());
        recipe.setDescription(updateRecipeDto.getDescription());
        recipe.setCuisine(updateRecipeDto.getCuisine());
        recipe.setMealType(updateRecipeDto.getMealType());
        recipe.setCookingTime(updateRecipeDto.getCookingTime());
        recipe.setDietType(updateRecipeDto.getDietType());
        recipe.setCookingMethod(updateRecipeDto.getCookingMethod());
        recipe.setDifficulty(updateRecipeDto.getDifficulty());
        recipe.setInstructions(updateRecipeDto.getInstructions());
        recipe.setImageUrl(updateRecipeDto.getImageUrl());

        // Update ingredients
        recipe.getIngredients().clear();
        if (updateRecipeDto.getIngredients() != null) {
            updateRecipeDto.getIngredients().forEach(ingredientDto -> {
                Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + ingredientDto.getIngredientId()));

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .quantity(ingredientDto.getQuantity())
                        .unit(ingredientDto.getUnit())
                        .notes(ingredientDto.getNotes())
                        .build();

                recipe.getIngredients().add(recipeIngredient);
            });
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        RecipeDetailDto detailDto = recipeMapper.recipeToRecipeDetailDto(updatedRecipe);

        // Set isFavorite flag
        boolean isFavorite = favoriteRecipeRepository.existsByUserAndRecipe(user, updatedRecipe);
        detailDto.setFavorite(isFavorite);

        return detailDto;
    }

    @Override
    @Transactional
    public RecipeDetailDto createRecipe(@Valid CreateRecipeDto createRecipeDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = Recipe.builder()
                .name(createRecipeDto.getName())
                .description(createRecipeDto.getDescription())
                .cuisine(createRecipeDto.getCuisine())
                .mealType(createRecipeDto.getMealType())
                .cookingTime(createRecipeDto.getCookingTime())
                .dietType(createRecipeDto.getDietType())
                .cookingMethod(createRecipeDto.getCookingMethod())
                .difficulty(createRecipeDto.getDifficulty())
                .instructions(createRecipeDto.getInstructions())
                .imageUrl(createRecipeDto.getImageUrl())
                .author(user)
                .rating(BigDecimal.ZERO)
                .popularity(0)
                .ingredients(new HashSet<>())
                .favoritedByUsers(new HashSet<>())
                .build();

        if (createRecipeDto.getIngredients() != null) {
            createRecipeDto.getIngredients().forEach(ingredientDto -> {
                Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + ingredientDto.getIngredientId()));

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredient(ingredient)
                        .quantity(ingredientDto.getQuantity())
                        .unit(ingredientDto.getUnit())
                        .notes(ingredientDto.getNotes())
                        .build();

                recipe.getIngredients().add(recipeIngredient);
            });
        }

        Recipe savedRecipe = recipeRepository.save(recipe);
        RecipeDetailDto detailDto = recipeMapper.recipeToRecipeDetailDto(savedRecipe);
        detailDto.setFavorite(false);

        return detailDto;
    }

    @Override
    public Page<RecipeCardDto> getUserRecipes(int page, int size, String search, String cuisine, String mealType, String sort, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Pageable pageable = createPageableForUserRecipes(page, size, sort);
        Specification<Recipe> spec = Specification.where(RecipeSpecification.hasAuthor(currentUser));

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(RecipeSpecification.nameContains(search));
        }
        if (cuisine != null && !cuisine.trim().isEmpty()) {
            try {
                CuisineType cuisineType = CuisineType.valueOf(cuisine.toUpperCase());
                spec = spec.and(RecipeSpecification.hasCuisine(cuisineType));
            } catch (IllegalArgumentException e) {
            }
        }
        if (mealType != null && !mealType.trim().isEmpty()) {
            try {
                MealType mealTypeEnum = MealType.valueOf(mealType.toUpperCase());
                spec = spec.and(RecipeSpecification.hasMealType(mealTypeEnum));
            } catch (IllegalArgumentException e) {
            }
        }

        Page<Recipe> recipePage = recipeRepository.findAll(spec, pageable);
        return recipePage.map(recipeMapper::recipeToRecipeCardDto);
    }

    private Pageable createPageableForUserRecipes(int page, int size, String sort) {
        Sort sortObj;

        switch (sort.toLowerCase()) {
            case "oldest":
                sortObj = Sort.by(Sort.Direction.ASC, "createdAt");
                break;
            case "name_asc":
                sortObj = Sort.by(Sort.Direction.ASC, "name");
                break;
            case "name_desc":
                sortObj = Sort.by(Sort.Direction.DESC, "name");
                break;
            case "rating":
                sortObj = Sort.by(Sort.Direction.DESC, "rating");
                break;
            case "popularity":
                sortObj = Sort.by(Sort.Direction.DESC, "popularity");
                break;
            case "newest":
            default:
                sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        return PageRequest.of(page, size, sortObj);
    }
}
