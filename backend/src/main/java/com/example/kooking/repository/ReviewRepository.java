package com.example.kooking.repository;

import com.example.kooking.model.Recipe;
import com.example.kooking.model.Review;
import com.example.kooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByRecipe(Recipe recipe, Pageable pageable);
    Page<Review> findByUser(User user, Pageable pageable);
    boolean existsByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.recipe.id = :recipeId")
    Double calculateAverageRatingByRecipe(@Param("recipeId") UUID recipeId);
}