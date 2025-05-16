package com.example.kooking.repository;

import com.example.kooking.model.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID>, JpaSpecificationExecutor<Ingredient> {
    boolean existsByNameIgnoreCase(String name);
    @NotNull Page<Ingredient> findAll(Specification<Ingredient> spec, @NotNull Pageable pageable);
}
