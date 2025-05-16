package com.example.kooking.utils;

import com.example.kooking.dto.RecipeCardDto;
import com.example.kooking.dto.RegisterDto;
import com.example.kooking.dto.UserPreferencesDto;
import com.example.kooking.dto.UserProfileDto;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import com.example.kooking.model.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true)
    User registerDtoToUser(RegisterDto registerDto);
    UserProfileDto userToUserProfileDto(User user);
    UserPreferencesDto userPreferencesToDto(UserPreferences userPreferences);
    RecipeCardDto recipeToRecipeCardDto(Recipe recipe);
}
