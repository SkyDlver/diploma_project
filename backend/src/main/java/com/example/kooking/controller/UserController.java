package com.example.kooking.controller;

import com.example.kooking.dto.*;
import com.example.kooking.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.getUserProfile(userEmail));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileDto updateUserProfileDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.updateUserProfile(userEmail, updateUserProfileDto));
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.getUserPreferences(userEmail));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<UserPreferencesDto> updateUserPreferences(
            @Valid @RequestBody UpdateUserPreferencesDto preferencesDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.updateUserPreferences(userEmail, preferencesDto));
    }

    @GetMapping("/me/favorite-recipes")
    public ResponseEntity<Page<RecipeCardDto>> getUserFavoriteRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.getUserFavoriteRecipes(userEmail, page, size));
    }

    @GetMapping("/me/recipes")
    public ResponseEntity<Page<RecipeCardDto>> getUserRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userServiceImpl.getUserRecipes(userEmail, page, size));
    }
}
