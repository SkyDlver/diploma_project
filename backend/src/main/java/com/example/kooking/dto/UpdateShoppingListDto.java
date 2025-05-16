package com.example.kooking.dto;

import com.example.kooking.enums.ShoppingStatus;
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
public class UpdateShoppingListDto {
    private Set<UUID> ingredientIds;
    private ShoppingStatus status;
}