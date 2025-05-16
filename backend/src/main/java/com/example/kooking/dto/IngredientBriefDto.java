package com.example.kooking.dto;

import com.example.kooking.enums.IngredientCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientBriefDto {
    private UUID id;
    private String name;
    private IngredientCategory category;
}