package com.example.kooking.service;

import com.example.kooking.dto.CreateReviewDto;
import com.example.kooking.dto.ReviewDto;
import com.example.kooking.dto.UpdateReviewDto;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.Review;
import com.example.kooking.model.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ReviewService {
    Page<ReviewDto> getRecipeReviews(UUID recipeId, int page, int size);

    ReviewDto createReview(UUID recipeId, CreateReviewDto createReviewDto, String userEmail);

    ReviewDto updateReview(UUID id, UpdateReviewDto updateReviewDto, String userEmail);

    void deleteReview(UUID id, String userEmail);

    Page<ReviewDto> getUserReviews(String userEmail, int page, int size);

    void updateRecipeRating(Recipe recipe);

    User findUserByEmail(String email);

    Recipe findRecipeById(UUID id);

    Review findReviewById(UUID id);
}
