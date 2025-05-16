package com.example.kooking.repository;

import com.example.kooking.enums.CuisineType;
import com.example.kooking.enums.MealType;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {
    @NotNull Page<Recipe> findAll(Specification<Recipe> specification, @NotNull Pageable pageable);
    Page<Recipe> findByAuthor(User user, Pageable pageable);
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
