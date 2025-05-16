package com.example.kooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDto {
    private UUID ingredientId;
    private String ingredientName;
    private Double quantity;
    private String unit;
    private String notes;
}