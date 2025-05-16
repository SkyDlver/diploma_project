package com.example.kooking.dto;

import com.example.kooking.enums.IngredientCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIngredientDto {
    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotNull(message = "Category is required")
    private IngredientCategory category;

    private String nutritionalValue;
}