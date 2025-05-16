package com.example.kooking.service.impl;

import com.example.kooking.dto.CreateIngredientDto;
import com.example.kooking.dto.IngredientDto;
import com.example.kooking.dto.UpdateIngredientDto;
import com.example.kooking.enums.IngredientCategory;
import com.example.kooking.model.Ingredient;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.utils.IngredientMapper;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientServiceImpl ingredientServiceImpl;

    private UUID id;
    private Ingredient ingredient;
    private IngredientDto ingredientDto;
    private CreateIngredientDto createIngredientDto;
    private UpdateIngredientDto updateIngredientDto;
    private Page<Ingredient> ingredientPage;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        // Setup ingredient
        ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName("Salt");
        ingredient.setCategory(IngredientCategory.SPICE);
        ingredient.setDescription("Common table salt");
        ingredient.setSubstitutes(new HashSet<>());
        ingredient.setSubstituteFor(new HashSet<>());

        // Setup ingredient DTO
        ingredientDto = new IngredientDto();
        ingredientDto.setId(id);
        ingredientDto.setName("Salt");
        ingredientDto.setCategory(IngredientCategory.SPICE);
        ingredientDto.setNutritionalValue("Common table salt");

        // Setup create DTO
        createIngredientDto = new CreateIngredientDto();
        createIngredientDto.setName("Salt");
        createIngredientDto.setCategory(IngredientCategory.SPICE);
        createIngredientDto.setNutritionalValue("Common table salt");

        // Setup update DTO
        updateIngredientDto = new UpdateIngredientDto();
        updateIngredientDto.setName("Sea Salt");
        updateIngredientDto.setCategory(IngredientCategory.SPICE);
        updateIngredientDto.setNutritionalValue("Common table salt");

        // Setup ingredients list
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);

        // Setup page
        ingredientPage = new PageImpl<>(ingredients);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllIngredients_NoFilters_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(ingredientRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(ingredientPage);
        when(ingredientMapper.ingredientToIngredientDto(any(Ingredient.class))).thenReturn(ingredientDto);

        // Act
        Page<IngredientDto> result = ingredientServiceImpl.getAllIngredients(0, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ingredientRepository).findAll(any(Specification.class), eq(pageable));
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllIngredients_WithSearchAndCategory_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(ingredientRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(ingredientPage);
        when(ingredientMapper.ingredientToIngredientDto(any(Ingredient.class))).thenReturn(ingredientDto);

        // Act
        Page<IngredientDto> result = ingredientServiceImpl.getAllIngredients(0, 10, "salt", IngredientCategory.SPICE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(ingredientRepository).findAll(any(Specification.class), eq(pageable));
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);
    }

    @Test
    void getIngredientById_Success() {
        // Arrange
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.ingredientToIngredientDto(ingredient)).thenReturn(ingredientDto);

        // Act
        IngredientDto result = ingredientServiceImpl.getIngredientById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Salt", result.getName());
        verify(ingredientRepository).findById(id);
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);
    }

    @Test
    void getIngredientById_NotFound() {
        // Arrange
        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredientServiceImpl.getIngredientById(id));
        verify(ingredientRepository).findById(id);
    }

    @Test
    void getAllCategories_Success() {
        // Act
        List<IngredientCategory> result = ingredientServiceImpl.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(IngredientCategory.values().length, result.size());
    }

    @Test
    void getIngredientSubstitutes_Success() {
        // Arrange
        Ingredient substitute = new Ingredient();
        substitute.setId(UUID.randomUUID());
        substitute.setName("Sugar");

        IngredientDto substituteDto = new IngredientDto();
        substituteDto.setId(substitute.getId());
        substituteDto.setName("Sugar");

        ingredient.getSubstitutes().add(substitute);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientMapper.ingredientToIngredientDto(substitute)).thenReturn(substituteDto);

        // Act
        List<IngredientDto> result = ingredientServiceImpl.getIngredientSubstitutes(id);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sugar", result.getFirst().getName());
        verify(ingredientRepository).findById(id);
        verify(ingredientMapper).ingredientToIngredientDto(substitute);
    }

    @Test
    void createIngredient_Success() {
        // Arrange
        when(ingredientRepository.existsByNameIgnoreCase("Salt")).thenReturn(false);
        when(ingredientMapper.createIngredientDtoToIngredient(createIngredientDto)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.ingredientToIngredientDto(ingredient)).thenReturn(ingredientDto);

        // Act
        IngredientDto result = ingredientServiceImpl.createIngredient(createIngredientDto);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Salt", result.getName());
        verify(ingredientRepository).existsByNameIgnoreCase("Salt");
        verify(ingredientMapper).createIngredientDtoToIngredient(createIngredientDto);
        verify(ingredientRepository).save(ingredient);
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);
    }

    @Test
    void createIngredient_NameAlreadyExists() {
        // Arrange
        when(ingredientRepository.existsByNameIgnoreCase("Salt")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ingredientServiceImpl.createIngredient(createIngredientDto));
        assertEquals("Ingredient with name Salt already exists", exception.getMessage());
        verify(ingredientRepository).existsByNameIgnoreCase("Salt");
        verifyNoMoreInteractions(ingredientMapper, ingredientRepository);
    }

    @Test
    void updateIngredient_Success() {
        // Arrange
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsByNameIgnoreCase("Sea Salt")).thenReturn(false);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.ingredientToIngredientDto(ingredient)).thenReturn(ingredientDto);

        // Act
        IngredientDto result = ingredientServiceImpl.updateIngredient(id, updateIngredientDto);

        // Assert
        assertNotNull(result);
        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).existsByNameIgnoreCase("Sea Salt");
        verify(ingredientMapper).updateIngredientFromDto(updateIngredientDto, ingredient);
        verify(ingredientRepository).save(ingredient);
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);
    }

    @Test
    void updateIngredient_NameAlreadyExists() {
        // Arrange
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsByNameIgnoreCase("Sea Salt")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ingredientServiceImpl.updateIngredient(id, updateIngredientDto));
        assertEquals("Ingredient with name Sea Salt already exists", exception.getMessage());
        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).existsByNameIgnoreCase("Sea Salt");
        verifyNoMoreInteractions(ingredientMapper, ingredientRepository);
    }

    @Test
    void deleteIngredient_Success() {
        // Arrange
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));

        // Act
        ingredientServiceImpl.deleteIngredient(id);

        // Assert
        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).delete(ingredient);
    }

    @Test
    void addSubstitute_Success() {
        // Arrange
        UUID substituteId = UUID.randomUUID();
        Ingredient substitute = new Ingredient();
        substitute.setId(substituteId);
        substitute.setName("Sugar");
        substitute.setSubstitutes(new HashSet<>());
        substitute.setSubstituteFor(new HashSet<>());

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.findById(substituteId)).thenReturn(Optional.of(substitute));
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientRepository.save(substitute)).thenReturn(substitute);
        when(ingredientMapper.ingredientToIngredientDto(ingredient)).thenReturn(ingredientDto);

        // Act
        IngredientDto result = ingredientServiceImpl.addSubstitute(id, substituteId);

        // Assert
        assertNotNull(result);
        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).findById(substituteId);
        verify(ingredientRepository).save(ingredient);
        verify(ingredientRepository).save(substitute);
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);

        assertTrue(ingredient.getSubstitutes().contains(substitute));
        assertTrue(substitute.getSubstituteFor().contains(ingredient));
    }

    @Test
    void addSubstitute_SameIngredient() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ingredientServiceImpl.addSubstitute(id, id));
        assertEquals("An ingredient cannot be a substitute for itself", exception.getMessage());
        verifyNoInteractions(ingredientRepository, ingredientMapper);
    }

    @Test
    void removeSubstitute_Success() {
        // Arrange
        UUID substituteId = UUID.randomUUID();
        Ingredient substitute = new Ingredient();
        substitute.setId(substituteId);
        substitute.setName("Sugar");
        substitute.setSubstitutes(new HashSet<>());
        substitute.setSubstituteFor(new HashSet<>());

        ingredient.getSubstitutes().add(substitute);
        substitute.getSubstituteFor().add(ingredient);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.findById(substituteId)).thenReturn(Optional.of(substitute));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient); // Use any() matcher
        when(ingredientMapper.ingredientToIngredientDto(ingredient)).thenReturn(ingredientDto);

        // Act
        IngredientDto result = ingredientServiceImpl.removeSubstitute(id, substituteId);

        // Assert
        assertNotNull(result);
        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).findById(substituteId);
        verify(ingredientRepository, times(2)).save(any(Ingredient.class)); // Verify 2 calls
        verify(ingredientMapper).ingredientToIngredientDto(ingredient);

        assertFalse(ingredient.getSubstitutes().contains(substitute));
        assertFalse(substitute.getSubstituteFor().contains(ingredient));
    }
}