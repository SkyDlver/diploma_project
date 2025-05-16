package com.example.kooking.repository;

import com.example.kooking.model.FavoriteRecipe;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, UUID> {
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    Optional<FavoriteRecipe> findByUserAndRecipe(User user, Recipe recipe);
    Page<FavoriteRecipe> findByUser(User user, Pageable pageable);
}
