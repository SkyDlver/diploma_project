package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.*;
import com.example.kooking.model.FavoriteRecipe;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import com.example.kooking.model.UserPreferences;
import com.example.kooking.repository.FavoriteRecipeRepository;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.UserPreferencesRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Recipe recipe;
    private UserPreferences userPreferences;
    private UserProfileDto userProfileDto;
    private UserPreferencesDto userPreferencesDto;
    private RecipeCardDto recipeCardDto;
    private UpdateUserProfileDto updateUserProfileDto;
    private UpdateUserPreferencesDto updateUserPreferencesDto;
    private FavoriteRecipe favoriteRecipe;

    @BeforeEach
    void setUp() {
        // Setup user preferences
        userPreferences = new UserPreferences();
        userPreferences.setId(UUID.randomUUID());
        userPreferences.setPreferredCuisine(Set.of(CuisineType.ITALIAN));
        userPreferences.setPreferredMealTypes(Set.of(MealType.DINNER));
        userPreferences.setDietaryRestrictions(Set.of(DietType.VEGETARIAN));
        userPreferences.setPreferredCookingMethods(Set.of(CookingMethod.BAKING));
        userPreferences.setPreferredDifficulty(DifficultyLevel.EASY);

        // Setup user
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserPreferences(userPreferences);

        // Setup recipe
        recipe = new Recipe();
        recipe.setId(UUID.randomUUID());
        recipe.setName("Test Recipe");
        recipe.setDescription("A test recipe");
        recipe.setCuisine(CuisineType.ITALIAN);
        recipe.setMealType(MealType.DINNER);
        recipe.setRating(BigDecimal.valueOf(4.5));
        recipe.setAuthor(user);

        // Setup favorite recipe
        favoriteRecipe = new FavoriteRecipe();
        favoriteRecipe.setId(UUID.randomUUID());
        favoriteRecipe.setUser(user);
        favoriteRecipe.setRecipe(recipe);

        // Setup user profile DTO
        userProfileDto = new UserProfileDto();
        userProfileDto.setId(user.getId());
        userProfileDto.setEmail("user@example.com");
        userProfileDto.setFirstName("Test");
        userProfileDto.setLastName("User");

        // Setup user preferences DTO
        userPreferencesDto = new UserPreferencesDto();
        userPreferencesDto.setPreferredCuisine(Set.of(CuisineType.ITALIAN));
        userPreferencesDto.setPreferredMealTypes(Set.of(MealType.DINNER));
        userPreferencesDto.setDietaryRestrictions(Set.of(DietType.VEGETARIAN));
        userPreferencesDto.setPreferredCookingMethods(Set.of(CookingMethod.BAKING));
        userPreferencesDto.setPreferredDifficulty(DifficultyLevel.EASY);

        // Setup recipe card DTO
        recipeCardDto = new RecipeCardDto();
        recipeCardDto.setId(recipe.getId());
        recipeCardDto.setName("Test Recipe");
        recipeCardDto.setDescription("A test recipe");
        recipeCardDto.setCuisine(CuisineType.ITALIAN);
        recipeCardDto.setMealType(MealType.DINNER);
        recipeCardDto.setRating(BigDecimal.valueOf(4.5));

        // Setup update user profile DTO
        updateUserProfileDto = new UpdateUserProfileDto();
        updateUserProfileDto.setFirstName("Updated");
        updateUserProfileDto.setLastName("Name");

        // Setup update user preferences DTO
        updateUserPreferencesDto = new UpdateUserPreferencesDto();
        updateUserPreferencesDto.setPreferredCuisine(Set.of(CuisineType.FRENCH));
        updateUserPreferencesDto.setPreferredMealTypes(Set.of(MealType.LUNCH));
        updateUserPreferencesDto.setDietaryRestrictions(Set.of(DietType.VEGAN));
        updateUserPreferencesDto.setPreferredCookingMethods(Set.of(CookingMethod.GRILLING));
        updateUserPreferencesDto.setPreferredDifficulty(DifficultyLevel.MEDIUM);
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserProfileDto(user)).thenReturn(userProfileDto);

        // Act
        UserProfileDto result = userService.getUserProfile("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(userProfileDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(userMapper).userToUserProfileDto(user);
    }

    @Test
    void getUserProfile_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserProfile("nonexistent@example.com"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserProfileDto(user)).thenReturn(userProfileDto);

        // Act
        UserProfileDto result = userService.updateUserProfile("user@example.com", updateUserProfileDto);

        // Assert
        assertNotNull(result);
        assertEquals(userProfileDto, result);
        assertEquals("Updated", user.getFirstName());
        assertEquals("Name", user.getLastName());

        verify(userRepository).findByEmail("user@example.com");
        verify(userRepository).save(user);
        verify(userMapper).userToUserProfileDto(user);
    }

    @Test
    void updateUserProfile_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.updateUserProfile("nonexistent@example.com", updateUserProfileDto));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserPreferences_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userPreferencesToDto(userPreferences)).thenReturn(userPreferencesDto);

        // Act
        UserPreferencesDto result = userService.getUserPreferences("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(userPreferencesDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(userMapper).userPreferencesToDto(userPreferences);
    }

    @Test
    void getUserPreferences_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserPreferences("nonexistent@example.com"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserPreferences_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userPreferencesToDto(userPreferences)).thenReturn(userPreferencesDto);

        // Act
        UserPreferencesDto result = userService.updateUserPreferences("user@example.com", updateUserPreferencesDto);

        // Assert
        assertNotNull(result);
        assertEquals(userPreferencesDto, result);
        assertEquals(Set.of(CuisineType.FRENCH), userPreferences.getPreferredCuisine());
        assertEquals(Set.of(MealType.LUNCH), userPreferences.getPreferredMealTypes());
        assertEquals(Set.of(DietType.VEGAN), userPreferences.getDietaryRestrictions());
        assertEquals(Set.of(CookingMethod.GRILLING), userPreferences.getPreferredCookingMethods());
        assertEquals(DifficultyLevel.MEDIUM, userPreferences.getPreferredDifficulty());

        verify(userRepository).findByEmail("user@example.com");
        verify(userPreferencesRepository).save(userPreferences);
        verify(userMapper).userPreferencesToDto(userPreferences);
    }

    @Test
    void updateUserPreferences_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.updateUserPreferences("nonexistent@example.com", updateUserPreferencesDto));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(userMapper, userPreferencesRepository);
    }

    @Test
    void getUserFavoriteRecipes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<FavoriteRecipe> favoriteRecipePage = new PageImpl<>(List.of(favoriteRecipe));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(favoriteRecipeRepository.findByUser(user, pageable)).thenReturn(favoriteRecipePage);
        when(userMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = userService.getUserFavoriteRecipes("user@example.com", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(userRepository).findByEmail("user@example.com");
        verify(favoriteRecipeRepository).findByUser(user, pageable);
        verify(userMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    void getUserFavoriteRecipes_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserFavoriteRecipes("nonexistent@example.com", 0, 10));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(favoriteRecipeRepository, userMapper);
    }

    @Test
    void getUserRecipes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findByAuthor(user, pageable)).thenReturn(recipePage);
        when(userMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = userService.getUserRecipes("user@example.com", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findByAuthor(user, pageable);
        verify(userMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    void getUserRecipes_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserRecipes("nonexistent@example.com", 0, 10));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(recipeRepository, userMapper);
    }
}