package com.example.kooking.service;

import com.example.kooking.dto.CreateIngredientDto;
import com.example.kooking.dto.IngredientDto;
import com.example.kooking.dto.UpdateIngredientDto;
import com.example.kooking.enums.IngredientCategory;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface IngredientService {
    Page<IngredientDto> getAllIngredients(int page, int size, String search, IngredientCategory category);

    IngredientDto getIngredientById(UUID id);

    List<IngredientCategory> getAllCategories();

    List<IngredientDto> getIngredientSubstitutes(UUID id);

    @Transactional
    IngredientDto createIngredient(CreateIngredientDto createIngredientDto);

    @Transactional
    IngredientDto updateIngredient(UUID id, UpdateIngredientDto updateIngredientDto);

    @Transactional
    void deleteIngredient(UUID id);

    @Transactional
    IngredientDto addSubstitute(UUID id, UUID substituteId);

    @Transactional
    IngredientDto removeSubstitute(UUID id, UUID substituteId);
}
