package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.*;
import com.example.kooking.model.*;
import com.example.kooking.repository.FavoriteRecipeRepository;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.RecipeMapper;
import com.example.kooking.utils.RecipeUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeUtility recipeUtility;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private UUID recipeId;
    private Recipe recipe;
    private RecipeCardDto recipeCardDto;
    private RecipeDetailDto recipeDetailDto;
    private RecipeBriefDto recipeBriefDto;
    private User user;
    private Ingredient ingredient;
    private CreateRecipeDto createRecipeDto;
    private UpdateRecipeDto updateRecipeDto;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();

        // Setup user
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Setup author profile DTO
        UserProfileDto authorProfileDto = new UserProfileDto();
        authorProfileDto.setId(user.getId());
        authorProfileDto.setEmail("user@example.com");
        authorProfileDto.setFirstName("Test");
        authorProfileDto.setLastName("User");


        // Setup ingredient
        ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setName("Salt");

        // Setup recipe
        recipe = Recipe.builder()
                .id(recipeId)
                .name("Test Recipe")
                .description("A test recipe")
                .cuisine(CuisineType.ITALIAN)
                .mealType(MealType.DINNER)
                .cookingTime(30)
                .dietType(DietType.VEGETARIAN)
                .cookingMethod(CookingMethod.BAKING)
                .difficulty(DifficultyLevel.EASY)
                .instructions("Test instructions")
                .imageUrl("http://example.com/image.jpg")
                .author(user)
                .rating(BigDecimal.valueOf(4.5))
                .popularity(10)
                .ingredients(new HashSet<>())
                .favoritedByUsers(new HashSet<>())
                .build();

        // Setup recipe card DTO
        recipeCardDto = new RecipeCardDto();
        recipeCardDto.setId(recipeId);
        recipeCardDto.setName("Test Recipe");
        recipeCardDto.setDescription("A test recipe");
        recipeCardDto.setCuisine(CuisineType.ITALIAN);
        recipeCardDto.setMealType(MealType.DINNER);
        recipeCardDto.setCookingTime(30);
        recipeCardDto.setDifficulty(DifficultyLevel.EASY);
        recipeCardDto.setImageUrl("http://example.com/image.jpg");
        recipeCardDto.setRating(BigDecimal.valueOf(4.5));

        // Setup recipe detail DTO
        recipeDetailDto = new RecipeDetailDto();
        recipeDetailDto.setId(recipeId);
        recipeDetailDto.setName("Test Recipe");
        recipeDetailDto.setDescription("A test recipe");
        recipeDetailDto.setCuisine(CuisineType.ITALIAN);
        recipeDetailDto.setMealType(MealType.DINNER);
        recipeDetailDto.setCookingTime(30);
        recipeDetailDto.setDietType(DietType.VEGETARIAN);
        recipeDetailDto.setCookingMethod(CookingMethod.BAKING);
        recipeDetailDto.setDifficulty(DifficultyLevel.EASY);
        recipeDetailDto.setInstructions("Test instructions");
        recipeDetailDto.setImageUrl("http://example.com/image.jpg");
        recipeDetailDto.setAuthor(authorProfileDto);
        recipeDetailDto.setRating(BigDecimal.valueOf(4.5));
        recipeDetailDto.setIngredients(new ArrayList<>());

        // Setup recipe brief DTO
        recipeBriefDto = new RecipeBriefDto();
        recipeBriefDto.setId(recipeId);
        recipeBriefDto.setName("Test Recipe");
        recipeBriefDto.setDescription("A test recipe");

        // Setup recipe ingredient DTO
        RecipeIngredientDto recipeIngredientDto = new RecipeIngredientDto();
        recipeIngredientDto.setIngredientId(ingredientId);
        recipeIngredientDto.setIngredientName("Salt");
        recipeIngredientDto.setQuantity(4.5);
        recipeIngredientDto.setUnit("tsp");

        // Setup create recipe DTO
        createRecipeDto = new CreateRecipeDto();
        createRecipeDto.setName("Test Recipe");
        createRecipeDto.setDescription("A test recipe");
        createRecipeDto.setCuisine(CuisineType.ITALIAN);
        createRecipeDto.setMealType(MealType.DINNER);
        createRecipeDto.setCookingTime(30);
        createRecipeDto.setDietType(DietType.VEGETARIAN);
        createRecipeDto.setCookingMethod(CookingMethod.BAKING);
        createRecipeDto.setDifficulty(DifficultyLevel.EASY);
        createRecipeDto.setInstructions("Test instructions");
        createRecipeDto.setImageUrl("http://example.com/image.jpg");
        createRecipeDto.setIngredients(List.of(recipeIngredientDto));

        // Setup update recipe DTO
        updateRecipeDto = new UpdateRecipeDto();
        updateRecipeDto.setName("Updated Recipe");
        updateRecipeDto.setDescription("An updated test recipe");
        updateRecipeDto.setCuisine(CuisineType.FRENCH);
        updateRecipeDto.setMealType(MealType.LUNCH);
        updateRecipeDto.setCookingTime(45);
        updateRecipeDto.setDietType(DietType.VEGAN);
        updateRecipeDto.setCookingMethod(CookingMethod.GRILLING);
        updateRecipeDto.setDifficulty(DifficultyLevel.EASY);
        updateRecipeDto.setInstructions("Updated instructions");
        updateRecipeDto.setImageUrl("http://example.com/updated-image.jpg");
        updateRecipeDto.setIngredients(List.of(recipeIngredientDto));
    }

    @Test
    void getRecipes_WithoutSearch_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(recipeRepository.findAll(pageable)).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = recipeService.getRecipes(0, 10, null, "name", Sort.Direction.ASC);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(recipeRepository).findAll(pageable);
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    void getRecipes_WithSearch_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(recipeRepository.findByNameContainingIgnoreCase("test", pageable)).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = recipeService.getRecipes(0, 10, "test", "name", Sort.Direction.ASC);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(recipeRepository).findByNameContainingIgnoreCase("test", pageable);
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    @SuppressWarnings("unchecked")
    void advancedSearch_Success() {
        // Arrange
        AdvancedRecipeFilterCriteria criteria = new AdvancedRecipeFilterCriteria();
        criteria.setCuisines(Collections.singletonList(CuisineType.ITALIAN));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(recipeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = recipeService.advancedSearch(0, 10, criteria, "name", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(recipeRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    void favoriteRecipe_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favoriteRecipeRepository.existsByUserAndRecipe(user, recipe)).thenReturn(false);

        // Act
        recipeService.favoriteRecipe(recipeId, "user@example.com");

        // Assert
        assertEquals(11, recipe.getPopularity()); // Popularity increased by 1
        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(favoriteRecipeRepository).existsByUserAndRecipe(user, recipe);
        verify(favoriteRecipeRepository).save(any(FavoriteRecipe.class));
    }

    @Test
    void favoriteRecipe_AlreadyFavorited() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favoriteRecipeRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> recipeService.favoriteRecipe(recipeId, "user@example.com"));

        assertEquals("Recipe is already in favorites", exception.getMessage());
        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(favoriteRecipeRepository).existsByUserAndRecipe(user, recipe);
        verifyNoMoreInteractions(favoriteRecipeRepository);
    }

    @Test
    void unfavoriteRecipe_Success() {
        // Arrange
        FavoriteRecipe favoriteRecipe = FavoriteRecipe.builder()
                .user(user)
                .recipe(recipe)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favoriteRecipeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(favoriteRecipe));

        // Act
        recipeService.unfavoriteRecipe(recipeId, "user@example.com");

        // Assert
        assertEquals(9, recipe.getPopularity()); // Popularity decreased by 1
        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(favoriteRecipeRepository).findByUserAndRecipe(user, recipe);
        verify(favoriteRecipeRepository).delete(favoriteRecipe);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void unfavoriteRecipe_NotFavorited() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(favoriteRecipeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> recipeService.unfavoriteRecipe(recipeId, "user@example.com"));

        assertEquals("Recipe is not in favorites", exception.getMessage());
        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(favoriteRecipeRepository).findByUserAndRecipe(user, recipe);
        verifyNoMoreInteractions(favoriteRecipeRepository, recipeRepository);
    }

    @Test
    void getTrendingRecipes_Success() {
        // Arrange
        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(recipeRepository.findAll(topTwenty)).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        List<RecipeCardDto> result = recipeService.getTrendingRecipes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipeCardDto, result.getFirst());

        verify(recipeRepository).findAll(topTwenty);
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getSeasonalRecipes_Success() {
        // Arrange
        Season currentSeason = Season.SUMMER;
        List<CuisineType> seasonalCuisines = List.of(CuisineType.ITALIAN, CuisineType.MEDITERRANEAN);
        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "popularity"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(recipeUtility.getCurrentSeason()).thenReturn(currentSeason);
        when(recipeUtility.getSeasonalCuisines(currentSeason)).thenReturn(seasonalCuisines);
        when(recipeRepository.findAll(any(Specification.class), eq(topTwenty))).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        List<RecipeCardDto> result = recipeService.getSeasonalRecipes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipeCardDto, result.getFirst());

        verify(recipeUtility).getCurrentSeason();
        verify(recipeUtility).getSeasonalCuisines(currentSeason);
        verify(recipeRepository).findAll(any(Specification.class), eq(topTwenty));
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRecommendedRecipes_EnoughRecipesForMealType() {
        // Arrange
        MealType currentMealType = MealType.DINNER;
        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "rating"));
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            recipes.add(recipe);
        }
        Page<Recipe> recipePage = new PageImpl<>(recipes);

        when(recipeUtility.getCurrentMealType()).thenReturn(currentMealType);
        when(recipeRepository.findAll(any(Specification.class), eq(topTwenty))).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(any(Recipe.class))).thenReturn(recipeCardDto);

        // Act
        List<RecipeCardDto> result = recipeService.getRecommendedRecipes();

        // Assert
        assertNotNull(result);
        assertEquals(20, result.size());

        verify(recipeUtility).getCurrentMealType();
        verify(recipeRepository).findAll(any(Specification.class), eq(topTwenty));
        verify(recipeMapper, times(20)).recipeToRecipeCardDto(any(Recipe.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRecommendedRecipes_NotEnoughRecipesForMealType() {
        // Arrange
        MealType currentMealType = MealType.DINNER;
        Pageable topTwenty = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "rating"));
        Pageable remaining = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "popularity"));

        List<Recipe> mealTypeRecipes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mealTypeRecipes.add(recipe);
        }

        List<Recipe> additionalRecipes = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            additionalRecipes.add(recipe);
        }

        Page<Recipe> mealTypePage = new PageImpl<>(mealTypeRecipes);
        Page<Recipe> additionalPage = new PageImpl<>(additionalRecipes);

        when(recipeUtility.getCurrentMealType()).thenReturn(currentMealType);
        when(recipeRepository.findAll(any(Specification.class), eq(topTwenty))).thenReturn(mealTypePage);
        when(recipeRepository.findAll(any(Specification.class), eq(remaining))).thenReturn(additionalPage);
        when(recipeMapper.recipeToRecipeCardDto(any(Recipe.class))).thenReturn(recipeCardDto);

        // Act
        List<RecipeCardDto> result = recipeService.getRecommendedRecipes();

        // Assert
        assertNotNull(result);
        assertEquals(20, result.size());

        verify(recipeUtility).getCurrentMealType();
        verify(recipeRepository, times(2)).findAll(any(Specification.class), any(Pageable.class));
        verify(recipeMapper, times(20)).recipeToRecipeCardDto(any(Recipe.class));
    }

    @Test
    void getRecipeById_Success() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.recipeToRecipeDetailDto(recipe)).thenReturn(recipeDetailDto);

        // Act
        RecipeDetailDto result = recipeService.getRecipeById(recipeId);

        // Assert
        assertNotNull(result);
        assertEquals(recipeDetailDto, result);

        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper).recipeToRecipeDetailDto(recipe);
    }

    @Test
    void getRecipeById_NotFound() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> recipeService.getRecipeById(recipeId));

        assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
        verify(recipeRepository).findById(recipeId);
        verifyNoInteractions(recipeMapper);
    }

    @Test
    void getRecipeBrief_Success() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.recipeToRecipeBriefDto(recipe)).thenReturn(recipeBriefDto);

        // Act
        RecipeBriefDto result = recipeService.getRecipeBrief(recipeId);

        // Assert
        assertNotNull(result);
        assertEquals(recipeBriefDto, result);

        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper).recipeToRecipeBriefDto(recipe);
    }

    @Test
    void deleteRecipe_Success() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        recipeService.deleteRecipe(recipeId);

        // Assert
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void updateRecipe_Success() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(any(UUID.class))).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.recipeToRecipeDetailDto(recipe)).thenReturn(recipeDetailDto);
        when(favoriteRecipeRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        // Act
        RecipeDetailDto result = recipeService.updateRecipe(recipeId, updateRecipeDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(recipeDetailDto, result);
        assertTrue(result.isFavorite());

        verify(recipeRepository).findById(recipeId);
        verify(userRepository).findByEmail("user@example.com");
        verify(ingredientRepository).findById(any(UUID.class));
        verify(recipeRepository).save(recipe);
        verify(recipeMapper).recipeToRecipeDetailDto(recipe);
        verify(favoriteRecipeRepository).existsByUserAndRecipe(user, recipe);
    }

    @Test
    void updateRecipe_NotAuthorized() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());
        differentUser.setEmail("different@example.com");

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail("different@example.com")).thenReturn(Optional.of(differentUser));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> recipeService.updateRecipe(recipeId, updateRecipeDto, "different@example.com"));

        assertEquals("You are not authorized to update this recipe", exception.getMessage());
        verify(recipeRepository).findById(recipeId);
        verify(userRepository).findByEmail("different@example.com");
        verifyNoMoreInteractions(recipeRepository, ingredientRepository, recipeMapper, favoriteRecipeRepository);
    }

    @Test
    void createRecipe_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(any(UUID.class))).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(recipeMapper.recipeToRecipeDetailDto(recipe)).thenReturn(recipeDetailDto);

        // Act
        RecipeDetailDto result = recipeService.createRecipe(createRecipeDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(recipeDetailDto, result);
        assertFalse(result.isFavorite());

        verify(userRepository).findByEmail("user@example.com");
        verify(ingredientRepository).findById(any(UUID.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).recipeToRecipeDetailDto(recipe);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserRecipes_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(recipePage);
        when(recipeMapper.recipeToRecipeCardDto(recipe)).thenReturn(recipeCardDto);

        // Act
        Page<RecipeCardDto> result = recipeService.getUserRecipes(0, 10, "test", "italian", "dinner", "newest", "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipeCardDto, result.getContent().getFirst());

        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findAll(any(Specification.class), eq(pageable));
        verify(recipeMapper).recipeToRecipeCardDto(recipe);
    }

    @Test
    void getUserRecipes_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> recipeService.getUserRecipes(0, 10, null, null, null, "newest", "nonexistent@example.com"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verifyNoInteractions(recipeRepository, recipeMapper);
    }
}