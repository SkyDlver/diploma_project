package com.example.kooking.service;

import com.example.kooking.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UserService {
    UserProfileDto getUserProfile(String userEmail);

    UserProfileDto updateUserProfile(String userEmail, @Valid UpdateUserProfileDto updateUserProfileDto);

    UserPreferencesDto updateUserPreferences(String userEmail, @Valid UpdateUserPreferencesDto preferencesDto);

    Page<RecipeCardDto> getUserFavoriteRecipes(String userEmail, int page, int size);

    Page<RecipeCardDto> getUserRecipes(String userEmail, int page, int size);
}
