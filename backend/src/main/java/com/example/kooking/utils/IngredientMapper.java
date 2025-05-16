package com.example.kooking.utils;

import com.example.kooking.dto.*;
import com.example.kooking.model.Ingredient;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    IngredientDto ingredientToIngredientDto(Ingredient ingredient);

    IngredientBriefDto ingredientToIngredientBriefDto(Ingredient ingredient);

    List<IngredientBriefDto> ingredientsToIngredientBriefDtos(List<Ingredient> ingredients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "substitutes", ignore = true)
    @Mapping(target = "substituteFor", ignore = true)
    Ingredient createIngredientDtoToIngredient(CreateIngredientDto createIngredientDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "substitutes", ignore = true)
    @Mapping(target = "substituteFor", ignore = true)
    void updateIngredientFromDto(UpdateIngredientDto updateIngredientDto, @MappingTarget Ingredient ingredient);
}