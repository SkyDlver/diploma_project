package com.example.kooking.controller;

import com.example.kooking.dto.*;
import com.example.kooking.service.impl.ShoppingListServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListServiceImpl shoppingListServiceImpl;

    @GetMapping("/user")
    public ResponseEntity<List<ShoppingListDto>> getUserShoppingLists(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.getUserShoppingLists(userEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListDto> getShoppingListById(@PathVariable UUID id, Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.getShoppingListById(id, userEmail));
    }

    @PostMapping
    public ResponseEntity<ShoppingListDto> createShoppingList(
            @Valid @RequestBody CreateShoppingListDto createShoppingListDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shoppingListServiceImpl.createShoppingList(createShoppingListDto, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingListDto> updateShoppingList(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShoppingListDto updateShoppingListDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.updateShoppingList(id, updateShoppingListDto, userEmail));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShoppingListDto> updateShoppingListStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShoppingStatusDto statusDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.updateShoppingListStatus(id, statusDto.getStatus(), userEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable UUID id, Authentication authentication) {
        String userEmail = authentication.getName();
        shoppingListServiceImpl.deleteShoppingList(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ingredients")
    public ResponseEntity<ShoppingListDto> addIngredientsToShoppingList(
            @PathVariable UUID id,
            @Valid @RequestBody AddIngredientsDto addIngredientsDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.addIngredientsToShoppingList(id, addIngredientsDto.getIngredientIds(), userEmail));
    }

    @DeleteMapping("/{id}/ingredients/{ingredientId}")
    public ResponseEntity<ShoppingListDto> removeIngredientFromShoppingList(
            @PathVariable UUID id,
            @PathVariable UUID ingredientId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(shoppingListServiceImpl.removeIngredientFromShoppingList(id, ingredientId, userEmail));
    }
}
