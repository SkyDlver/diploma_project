package com.example.kooking.utils;

import com.example.kooking.dto.*;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.RecipeIngredient;
import org.mapstruct.*;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {IngredientMapper.class})
public interface RecipeMapper {
    // Remove the ingredients mapping for RecipeCardDto
    RecipeCardDto recipeToRecipeCardDto(Recipe recipe);

    // Keep the ingredients mapping for RecipeDetailDto
    @Mapping(target = "ingredients", source = "ingredients")
    RecipeDetailDto recipeToRecipeDetailDto(Recipe recipe);

    RecipeBriefDto recipeToRecipeBriefDto(Recipe recipe);

    @Named("mapRecipeIngredient")
    default RecipeIngredientDto mapRecipeIngredient(RecipeIngredient recipeIngredient) {
        if (recipeIngredient == null) {
            return null;
        }

        return RecipeIngredientDto.builder()
                .ingredientId(recipeIngredient.getIngredient().getId())
                .ingredientName(recipeIngredient.getIngredient().getName())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getUnit())
                .notes(recipeIngredient.getNotes())
                .build();
    }

    @IterableMapping(qualifiedByName = "mapRecipeIngredient")
    default List<RecipeIngredientDto> mapRecipeIngredients(Set<RecipeIngredient> ingredients) {
        if (ingredients == null) {
            return Collections.emptyList();
        }

        return ingredients.stream()
                .map(this::mapRecipeIngredient)
                .collect(Collectors.toList());
    }
}