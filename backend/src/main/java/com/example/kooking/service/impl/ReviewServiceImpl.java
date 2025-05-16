package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.service.ReviewService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.Review;
import com.example.kooking.model.User;
import com.example.kooking.repository.RecipeRepository;
import com.example.kooking.repository.ReviewRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Page<ReviewDto> getRecipeReviews(UUID recipeId, int page, int size) {
        Recipe recipe = findRecipeById(recipeId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByRecipe(recipe, pageable)
                .map(reviewMapper::reviewToReviewDto);
    }

    @Override
    @Transactional
    public ReviewDto createReview(UUID recipeId, CreateReviewDto createReviewDto, String userEmail) {
        User user = findUserByEmail(userEmail);
        Recipe recipe = findRecipeById(recipeId);
        if (reviewRepository.existsByUserAndRecipe(user, recipe)) {
            throw new IllegalStateException("You have already reviewed this recipe");
        }

        Review review = reviewMapper.createReviewDtoToReview(createReviewDto);
        review.setUser(user);
        review.setRecipe(recipe);

        Review savedReview = reviewRepository.save(review);

        updateRecipeRating(recipe);

        return reviewMapper.reviewToReviewDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(UUID id, UpdateReviewDto updateReviewDto, String userEmail) {
        User currentUser = findUserByEmail(userEmail);
        Review review = findReviewById(id);

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        reviewMapper.updateReviewFromDto(updateReviewDto, review);
        Review updatedReview = reviewRepository.save(review);

        updateRecipeRating(review.getRecipe());

        return reviewMapper.reviewToReviewDto(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(UUID id, String userEmail) {
        User currentUser = findUserByEmail(userEmail);
        Review review = findReviewById(id);
        Recipe recipe = review.getRecipe();

        if (!review.getUser().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        reviewRepository.delete(review);
        updateRecipeRating(recipe);
    }

    @Override
    public Page<ReviewDto> getUserReviews(String userEmail, int page, int size) {
        User user = findUserByEmail(userEmail);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUser(user, pageable)
                .map(reviewMapper::reviewToReviewDto);
    }

    @Override
    public void updateRecipeRating(Recipe recipe) {
        Double averageRating = reviewRepository.calculateAverageRatingByRecipe(recipe.getId());
        if (averageRating != null) {
            BigDecimal rating = BigDecimal.valueOf(averageRating)
                    .setScale(1, RoundingMode.HALF_UP);
            recipe.setRating(rating);
        } else {
            recipe.setRating(BigDecimal.ZERO);
        }
        recipeRepository.save(recipe);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public Recipe findRecipeById(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    @Override
    public Review findReviewById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }
}