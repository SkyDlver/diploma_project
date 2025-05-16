package com.example.kooking.controller;

import com.example.kooking.dto.*;
import com.example.kooking.enums.IngredientCategory;
import com.example.kooking.service.impl.IngredientServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientServiceImpl ingredientServiceImpl;

    @GetMapping
    public ResponseEntity<Page<IngredientDto>> getAllIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) IngredientCategory category) {

        return ResponseEntity.ok(ingredientServiceImpl.getAllIngredients(page, size, search, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable UUID id) {
        return ResponseEntity.ok(ingredientServiceImpl.getIngredientById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<IngredientCategory>> getAllCategories() {
        return ResponseEntity.ok(ingredientServiceImpl.getAllCategories());
    }

    @GetMapping("/{id}/substitutes")
    public ResponseEntity<List<IngredientDto>> getIngredientSubstitutes(@PathVariable UUID id) {
        return ResponseEntity.ok(ingredientServiceImpl.getIngredientSubstitutes(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@Valid @RequestBody CreateIngredientDto createIngredientDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ingredientServiceImpl.createIngredient(createIngredientDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIngredientDto updateIngredientDto) {
        return ResponseEntity.ok(ingredientServiceImpl.updateIngredient(id, updateIngredientDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        ingredientServiceImpl.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/substitutes/{substituteId}")
    public ResponseEntity<IngredientDto> addSubstitute(
            @PathVariable UUID id,
            @PathVariable UUID substituteId) {
        return ResponseEntity.ok(ingredientServiceImpl.addSubstitute(id, substituteId));
    }

    @DeleteMapping("/{id}/substitutes/{substituteId}")
    public ResponseEntity<IngredientDto> removeSubstitute(
            @PathVariable UUID id,
            @PathVariable UUID substituteId) {
        return ResponseEntity.ok(ingredientServiceImpl.removeSubstitute(id, substituteId));
    }
}