package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.model.FavoriteRecipe;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import com.example.kooking.model.UserPreferences;
import com.example.kooking.repository.FavoriteRecipeRepository;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.UserPreferencesRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.service.UserService;
import com.example.kooking.utils.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserMapper userMapper;
    private final FavoriteRecipeRepository favoriteRecipeRepository;

    @Override
    public UserProfileDto getUserProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.userToUserProfileDto(user);
    }

    @Override
    public UserProfileDto updateUserProfile(String userEmail, @Valid UpdateUserProfileDto updateUserProfileDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setFirstName(updateUserProfileDto.getFirstName());
        user.setLastName(updateUserProfileDto.getLastName());
        userRepository.save(user);
        return userMapper.userToUserProfileDto(user);
    }

    public UserPreferencesDto getUserPreferences(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.userPreferencesToDto(user.getUserPreferences());
    }

    @Override
    public UserPreferencesDto updateUserPreferences(String userEmail, @Valid UpdateUserPreferencesDto preferencesDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserPreferences preferences = user.getUserPreferences();

        preferences.setPreferredCuisine(preferencesDto.getPreferredCuisine());
        preferences.setPreferredMealTypes(preferencesDto.getPreferredMealTypes());
        preferences.setDietaryRestrictions(preferencesDto.getDietaryRestrictions());
        preferences.setPreferredCookingMethods(preferencesDto.getPreferredCookingMethods());
        preferences.setPreferredDifficulty(preferencesDto.getPreferredDifficulty());

        userPreferencesRepository.save(preferences);
        return userMapper.userPreferencesToDto(preferences);
    }

    @Override
    public Page<RecipeCardDto> getUserFavoriteRecipes(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Page<FavoriteRecipe> favoriteRecipes = favoriteRecipeRepository.findByUser(user, PageRequest.of(page, size));
        return favoriteRecipes.map(favorite -> userMapper.recipeToRecipeCardDto(favorite.getRecipe()));
    }

    @Override
    public Page<RecipeCardDto> getUserRecipes(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Page<Recipe> userRecipes = recipeRepository.findByAuthor(user, PageRequest.of(page, size));

        return userRecipes.map(userMapper::recipeToRecipeCardDto);
    }
}
