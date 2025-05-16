package com.example.kooking.controller;

import com.example.kooking.dto.CreateReviewDto;
import com.example.kooking.dto.ReviewDto;
import com.example.kooking.dto.UpdateReviewDto;
import com.example.kooking.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewServiceImpl;

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Page<ReviewDto>> getRecipeReviews(
            @PathVariable UUID recipeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewServiceImpl.getRecipeReviews(recipeId, page, size));
    }

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable UUID recipeId,
            @Valid @RequestBody CreateReviewDto createReviewDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewServiceImpl.createReview(recipeId, createReviewDto, userEmail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReviewDto updateReviewDto,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reviewServiceImpl.updateReview(id, updateReviewDto, userEmail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        reviewServiceImpl.deleteReview(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ReviewDto>> getUserReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(reviewServiceImpl.getUserReviews(userEmail, page, size));
    }
}
