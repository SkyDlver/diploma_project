package com.example.kooking.utils;

import com.example.kooking.dto.AdvancedRecipeFilterCriteria;
import com.example.kooking.dto.RecipeFilterCriteria;
import com.example.kooking.enums.CuisineType;
import com.example.kooking.enums.MealType;
import com.example.kooking.model.Ingredient;
import com.example.kooking.model.Recipe;
import com.example.kooking.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeSpecification {

    public static Specification<Recipe> advancedFilterByCriteria(AdvancedRecipeFilterCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getSearch() != null && !criteria.getSearch().trim().isEmpty()) {
                predicates.add(builder.like(
                        builder.lower(root.get("name")),
                        "%" + criteria.getSearch().toLowerCase() + "%"
                ));
            }

            // Handle included ingredients with join and exists subquery
            if (criteria.getIncludeIngredients() != null && !criteria.getIncludeIngredients().isEmpty()) {
                // For each ingredient that should be included
                for (UUID ingredientId : criteria.getIncludeIngredients()) {
                    // Create a subquery
                    Subquery<Recipe> subquery = query.subquery(Recipe.class);
                    Root<Recipe> subRoot = subquery.from(Recipe.class);
                    Join<Recipe, Ingredient> ingredientJoin = subRoot.join("ingredients");

                    // Add a predicate to check if the recipe has this ingredient
                    subquery.select(subRoot)
                            .where(
                                    builder.and(
                                            builder.equal(subRoot.get("id"), root.get("id")),
                                            builder.equal(ingredientJoin.get("id"), ingredientId)
                                    )
                            );

                    predicates.add(builder.exists(subquery));
                }
            }

            // Handle excluded ingredients with join and not exists subquery
            if (criteria.getExcludeIngredients() != null && !criteria.getExcludeIngredients().isEmpty()) {
                for (UUID ingredientId : criteria.getExcludeIngredients()) {
                    // Create a subquery
                    Subquery<Recipe> subquery = query.subquery(Recipe.class);
                    Root<Recipe> subRoot = subquery.from(Recipe.class);
                    Join<Recipe, Ingredient> ingredientJoin = subRoot.join("ingredients");

                    // Add a predicate to check if the recipe has this ingredient
                    subquery.select(subRoot)
                            .where(
                                    builder.and(
                                            builder.equal(subRoot.get("id"), root.get("id")),
                                            builder.equal(ingredientJoin.get("id"), ingredientId)
                                    )
                            );

                    // We want recipes that do NOT have this ingredient
                    predicates.add(builder.not(builder.exists(subquery)));
                }
            }

            // Rest of your criteria handling remains the same
            if (criteria.getMaxCookingTime() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("cookingTime"), criteria.getMaxCookingTime()));
            }

            if (criteria.getCuisines() != null && !criteria.getCuisines().isEmpty()) {
                predicates.add(root.get("cuisine").in(criteria.getCuisines()));
            }

            if (criteria.getMealTypes() != null && !criteria.getMealTypes().isEmpty()) {
                predicates.add(root.get("mealType").in(criteria.getMealTypes()));
            }

            if (criteria.getDietTypes() != null && !criteria.getDietTypes().isEmpty()) {
                predicates.add(root.get("dietType").in(criteria.getDietTypes()));
            }

            if (criteria.getCookingMethods() != null && !criteria.getCookingMethods().isEmpty()) {
                predicates.add(root.get("cookingMethod").in(criteria.getCookingMethods()));
            }

            if (criteria.getDifficulties() != null && !criteria.getDifficulties().isEmpty()) {
                predicates.add(root.get("difficulty").in(criteria.getDifficulties()));
            }

            // Important: For count queries, we need to make them distinct to avoid duplicates
            query.distinct(true);

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Recipe> filterByCriteria(RecipeFilterCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getSearch() != null) {
                predicates.add(builder.like(root.get("name"), "%" + criteria.getSearch() + "%"));
            }
            if (criteria.getCuisine() != null) {
                predicates.add(builder.equal(root.get("cuisine"), criteria.getCuisine()));
            }
            if (criteria.getMealType() != null) {
                predicates.add(builder.equal(root.get("mealType"), criteria.getMealType()));
            }
            if (criteria.getDietType() != null) {
                predicates.add(builder.equal(root.get("dietType"), criteria.getDietType()));
            }
            if (criteria.getCookingMethod() != null) {
                predicates.add(builder.equal(root.get("cookingMethod"), criteria.getCookingMethod()));
            }
            if (criteria.getDifficulty() != null) {
                predicates.add(builder.equal(root.get("difficulty"), criteria.getDifficulty()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Recipe> hasAuthor(User author) {
        return (root, query, cb) -> cb.equal(root.get("author"), author);
    }

    public static Specification<Recipe> nameContains(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Recipe> hasCuisine(CuisineType cuisineType) {
        return (root, query, cb) -> cb.equal(root.get("cuisine"), cuisineType);
    }

    public static Specification<Recipe> hasMealType(MealType mealType) {
        return (root, query, cb) -> cb.equal(root.get("mealType"), mealType);
    }
}

