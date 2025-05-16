package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.IngredientCategory;
import com.example.kooking.service.IngredientService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.example.kooking.model.Ingredient;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.utils.IngredientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    public Page<IngredientDto> getAllIngredients(int page, int size, String search, IngredientCategory category) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Ingredient> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }

        if (category != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), category));
        }

        return ingredientRepository.findAll(spec, pageable)
                .map(ingredientMapper::ingredientToIngredientDto);
    }

    @Override
    public IngredientDto getIngredientById(UUID id) {
        Ingredient ingredient = findIngredientById(id);
        return ingredientMapper.ingredientToIngredientDto(ingredient);
    }

    @Override
    public List<IngredientCategory> getAllCategories() {
        return Arrays.asList(IngredientCategory.values());
    }

    @Override
    public List<IngredientDto> getIngredientSubstitutes(UUID id) {
        Ingredient ingredient = findIngredientById(id);
        return ingredient.getSubstitutes().stream()
                .map(ingredientMapper::ingredientToIngredientDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public IngredientDto createIngredient(CreateIngredientDto createIngredientDto) {
        // Check if ingredient with the same name already exists
        if (ingredientRepository.existsByNameIgnoreCase(createIngredientDto.getName())) {
            throw new IllegalArgumentException("Ingredient with name " + createIngredientDto.getName() + " already exists");
        }

        Ingredient ingredient = ingredientMapper.createIngredientDtoToIngredient(createIngredientDto);
        ingredient.setSubstitutes(new HashSet<>());
        ingredient.setSubstituteFor(new HashSet<>());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.ingredientToIngredientDto(savedIngredient);
    }

    @Override
    @Transactional
    public IngredientDto updateIngredient(UUID id, UpdateIngredientDto updateIngredientDto) {
        Ingredient ingredient = findIngredientById(id);

        // Check if updated name conflicts with existing ingredient
        if (!ingredient.getName().equalsIgnoreCase(updateIngredientDto.getName()) &&
                ingredientRepository.existsByNameIgnoreCase(updateIngredientDto.getName())) {
            throw new IllegalArgumentException("Ingredient with name " + updateIngredientDto.getName() + " already exists");
        }

        ingredientMapper.updateIngredientFromDto(updateIngredientDto, ingredient);
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.ingredientToIngredientDto(updatedIngredient);
    }

    @Override
    @Transactional
    public void deleteIngredient(UUID id) {
        Ingredient ingredient = findIngredientById(id);

        // Remove this ingredient from all substitutes relationships
        for (Ingredient substitute : ingredient.getSubstitutes()) {
            substitute.getSubstituteFor().remove(ingredient);
        }

        for (Ingredient substituteFor : ingredient.getSubstituteFor()) {
            substituteFor.getSubstitutes().remove(ingredient);
        }

        ingredientRepository.delete(ingredient);
    }

    @Override
    @Transactional
    public IngredientDto addSubstitute(UUID id, UUID substituteId) {
        if (id.equals(substituteId)) {
            throw new IllegalArgumentException("An ingredient cannot be a substitute for itself");
        }

        Ingredient ingredient = findIngredientById(id);
        Ingredient substitute = findIngredientById(substituteId);

        ingredient.getSubstitutes().add(substitute);
        substitute.getSubstituteFor().add(ingredient);

        ingredientRepository.save(ingredient);
        ingredientRepository.save(substitute);

        return ingredientMapper.ingredientToIngredientDto(ingredient);
    }

    @Override
    @Transactional
    public IngredientDto removeSubstitute(UUID id, UUID substituteId) {
        Ingredient ingredient = findIngredientById(id);
        Ingredient substitute = findIngredientById(substituteId);

        ingredient.getSubstitutes().remove(substitute);
        substitute.getSubstituteFor().remove(ingredient);

        ingredientRepository.save(ingredient);
        ingredientRepository.save(substitute);

        return ingredientMapper.ingredientToIngredientDto(ingredient);
    }

    private Ingredient findIngredientById(UUID id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
    }
}
