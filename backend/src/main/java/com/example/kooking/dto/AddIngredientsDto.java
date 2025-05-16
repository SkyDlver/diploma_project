package com.example.kooking.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddIngredientsDto {
    @NotEmpty(message = "At least one ingredient is required")
    private Set<UUID> ingredientIds;
}