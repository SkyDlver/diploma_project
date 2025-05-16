package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.Review;
import com.example.kooking.model.User;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.ReviewRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.ReviewMapper;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private UUID recipeId;
    private UUID reviewId;
    private User user;
    private Recipe recipe;
    private Review review;
    private ReviewDto reviewDto;
    private CreateReviewDto createReviewDto;
    private UpdateReviewDto updateReviewDto;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Setup user
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Setup recipe
        recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setName("Test Recipe");
        recipe.setRating(BigDecimal.valueOf(4.5));

        // Setup review
        review = new Review();
        review.setId(reviewId);
        review.setUser(user);
        review.setRecipe(recipe);
        review.setRating(5);
        review.setComment("Great recipe!");
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        // Setup user profile DTO
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(userId);
        userProfileDto.setEmail("user@example.com");
        userProfileDto.setFirstName("Test");
        userProfileDto.setLastName("User");

        // Setup recipe brief DTO
        RecipeBriefDto recipeBriefDto = new RecipeBriefDto();
        recipeBriefDto.setId(recipeId);
        recipeBriefDto.setName("Test Recipe");
        recipeBriefDto.setDescription("A test recipe");

        // Setup review DTO
        reviewDto = new ReviewDto();
        reviewDto.setId(reviewId);
        reviewDto.setUser(userProfileDto);
        reviewDto.setRecipe(recipeBriefDto);
        reviewDto.setRating(5);
        reviewDto.setComment("Great recipe!");
        reviewDto.setCreatedAt(now);
        reviewDto.setUpdatedAt(now);

        // Setup create review DTO
        createReviewDto = new CreateReviewDto();
        createReviewDto.setRating(5);
        createReviewDto.setComment("Great recipe!");

        // Setup update review DTO
        updateReviewDto = new UpdateReviewDto();
        updateReviewDto.setRating(4);
        updateReviewDto.setComment("Good recipe, but could use more spices.");
    }

    @Test
    void getRecipeReviews_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = new PageImpl<>(List.of(review));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(reviewRepository.findByRecipe(recipe, pageable)).thenReturn(reviewPage);
        when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);

        // Act
        Page<ReviewDto> result = reviewService.getRecipeReviews(recipeId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(reviewDto, result.getContent().getFirst());

        verify(recipeRepository).findById(recipeId);
        verify(reviewRepository).findByRecipe(recipe, pageable);
        verify(reviewMapper).reviewToReviewDto(review);
    }

    @Test
    void getRecipeReviews_RecipeNotFound() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getRecipeReviews(recipeId, 0, 10));

        assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
        verify(recipeRepository).findById(recipeId);
        verifyNoInteractions(reviewMapper);
    }

    @Test
    void createReview_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(reviewRepository.existsByUserAndRecipe(user, recipe)).thenReturn(false);
        when(reviewMapper.createReviewDtoToReview(createReviewDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);
        when(reviewRepository.calculateAverageRatingByRecipe(recipeId)).thenReturn(5.0);

        // Act
        ReviewDto result = reviewService.createReview(recipeId, createReviewDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(reviewDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(reviewRepository).existsByUserAndRecipe(user, recipe);
        verify(reviewMapper).createReviewDtoToReview(createReviewDto);
        verify(reviewRepository).save(review);
        verify(reviewMapper).reviewToReviewDto(review);
        verify(reviewRepository).calculateAverageRatingByRecipe(recipeId);
        verify(recipeRepository).save(recipe);

        assertEquals(BigDecimal.valueOf(5.0).setScale(1, RoundingMode.HALF_UP), recipe.getRating());

    }

    @Test
    void createReview_AlreadyReviewed() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(reviewRepository.existsByUserAndRecipe(user, recipe)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(recipeId, createReviewDto, "user@example.com"));

        assertEquals("You have already reviewed this recipe", exception.getMessage());
        verify(userRepository).findByEmail("user@example.com");
        verify(recipeRepository).findById(recipeId);
        verify(reviewRepository).existsByUserAndRecipe(user, recipe);
        verifyNoMoreInteractions(reviewMapper, reviewRepository, recipeRepository);
    }

    @Test
    void updateReview_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);
        when(reviewRepository.calculateAverageRatingByRecipe(recipeId)).thenReturn(4.0);

        // Act
        ReviewDto result = reviewService.updateReview(reviewId, updateReviewDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(reviewDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(reviewRepository).findById(reviewId);
        verify(reviewMapper).updateReviewFromDto(updateReviewDto, review);
        verify(reviewRepository).save(review);
        verify(reviewMapper).reviewToReviewDto(review);
        verify(reviewRepository).calculateAverageRatingByRecipe(recipeId);
        verify(recipeRepository).save(recipe);

        assertEquals(BigDecimal.valueOf(4.0).setScale(1, RoundingMode.HALF_UP), recipe.getRating());
    }

    @Test
    void updateReview_NotAuthorized() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());
        differentUser.setEmail("different@example.com");

        when(userRepository.findByEmail("different@example.com")).thenReturn(Optional.of(differentUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> reviewService.updateReview(reviewId, updateReviewDto, "different@example.com"));

        assertEquals("You are not authorized to update this review", exception.getMessage());
        verify(userRepository).findByEmail("different@example.com");
        verify(reviewRepository).findById(reviewId);
        verifyNoMoreInteractions(reviewMapper, reviewRepository, recipeRepository);
    }

    @Test
    void deleteReview_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.calculateAverageRatingByRecipe(recipeId)).thenReturn(null);

        // Act
        reviewService.deleteReview(reviewId, "user@example.com");

        // Assert
        verify(userRepository).findByEmail("user@example.com");
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).delete(review);
        verify(reviewRepository).calculateAverageRatingByRecipe(recipeId);
        verify(recipeRepository).save(recipe);

        assertEquals(BigDecimal.ZERO, recipe.getRating());
    }

    @Test
    void deleteReview_NotAuthorized() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());
        differentUser.setEmail("different@example.com");

        when(userRepository.findByEmail("different@example.com")).thenReturn(Optional.of(differentUser));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> reviewService.deleteReview(reviewId, "different@example.com"));

        assertEquals("You are not authorized to update this review", exception.getMessage());
        verify(userRepository).findByEmail("different@example.com");
        verify(reviewRepository).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository, recipeRepository);
    }

    @Test
    void getUserReviews_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = new PageImpl<>(List.of(review));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(reviewRepository.findByUser(user, pageable)).thenReturn(reviewPage);
        when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);

        // Act
        Page<ReviewDto> result = reviewService.getUserReviews("user@example.com", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(reviewDto, result.getContent().getFirst());

        verify(userRepository).findByEmail("user@example.com");
        verify(reviewRepository).findByUser(user, pageable);
        verify(reviewMapper).reviewToReviewDto(review);
    }

    @Test
    void updateRecipeRating_WithReviews() {
        // Arrange
        when(reviewRepository.calculateAverageRatingByRecipe(recipeId)).thenReturn(4.7);

        // Act
        reviewService.updateRecipeRating(recipe);

        // Assert
        assertEquals(BigDecimal.valueOf(4.7).setScale(1, RoundingMode.HALF_UP), recipe.getRating());
        verify(reviewRepository).calculateAverageRatingByRecipe(recipeId);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void updateRecipeRating_NoReviews() {
        // Arrange
        when(reviewRepository.calculateAverageRatingByRecipe(recipeId)).thenReturn(null);

        // Act
        reviewService.updateRecipeRating(recipe);

        // Assert
        assertEquals(BigDecimal.ZERO, recipe.getRating());
        verify(reviewRepository).calculateAverageRatingByRecipe(recipeId);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void findUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        User result = reviewService.findUserByEmail("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void findUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> reviewService.findUserByEmail("nonexistent@example.com"));

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void findRecipeById_Success() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        Recipe result = reviewService.findRecipeById(recipeId);

        // Assert
        assertNotNull(result);
        assertEquals(recipe, result);
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void findRecipeById_NotFound() {
        // Arrange
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.findRecipeById(recipeId));

        assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void findReviewById_Success() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        Review result = reviewService.findReviewById(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(review, result);
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void findReviewById_NotFound() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.findReviewById(reviewId));

        assertEquals("Review not found with id: " + reviewId, exception.getMessage());
        verify(reviewRepository).findById(reviewId);
    }
}