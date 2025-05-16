package com.example.kooking.service;

import com.example.kooking.dto.CreateShoppingListDto;
import com.example.kooking.dto.ShoppingListDto;
import com.example.kooking.dto.UpdateShoppingListDto;
import com.example.kooking.enums.ShoppingStatus;
import com.example.kooking.model.Ingredient;
import com.example.kooking.model.ShoppingList;
import com.example.kooking.model.User;

import java.util.Set;
import java.util.UUID;

public interface ShoppingListService {
    ShoppingListDto createShoppingList(CreateShoppingListDto createShoppingListDto, String userEmail);

    ShoppingListDto updateShoppingList(UUID id, UpdateShoppingListDto updateShoppingListDto, String userEmail);

    ShoppingListDto updateShoppingListStatus(UUID id, ShoppingStatus status, String userEmail);

    void deleteShoppingList(UUID id, String userEmail);

    ShoppingListDto addIngredientsToShoppingList(UUID id, Set<UUID> ingredientIds, String userEmail);

    ShoppingListDto removeIngredientFromShoppingList(UUID id, UUID ingredientId, String userEmail);

    User findUserByEmail(String email);

    ShoppingList findShoppingListByIdAndUser(UUID id, User user);

    Set<Ingredient> findIngredientsByIds(Set<UUID> ingredientIds);
}
