package com.example.kooking.dto;

import com.example.kooking.enums.ShoppingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDto {
    private UUID id;
    private UserProfileDto user;
    private Set<IngredientBriefDto> ingredients;
    private ShoppingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}