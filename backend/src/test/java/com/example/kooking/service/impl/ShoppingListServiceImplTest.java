package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.IngredientCategory;
import com.example.kooking.enums.ShoppingStatus;
import com.example.kooking.model.Ingredient;
import com.example.kooking.model.ShoppingList;
import com.example.kooking.model.User;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.repository.ShoppingListRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.ShoppingListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ShoppingListMapper shoppingListMapper;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    private UUID shoppingListId;
    private UUID ingredientId1;
    private UUID ingredientId2;
    private User user;
    private Ingredient ingredient1;
    private Ingredient ingredient2;
    private ShoppingList shoppingList;
    private ShoppingListDto shoppingListDto;
    private CreateShoppingListDto createShoppingListDto;
    private UpdateShoppingListDto updateShoppingListDto;

    @BeforeEach
    void setUp() {
        shoppingListId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ingredientId1 = UUID.randomUUID();
        ingredientId2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Setup user
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Setup ingredients
        ingredient1 = new Ingredient();
        ingredient1.setId(ingredientId1);
        ingredient1.setName("Salt");
        ingredient1.setCategory(IngredientCategory.SPICE);

        ingredient2 = new Ingredient();
        ingredient2.setId(ingredientId2);
        ingredient2.setName("Pepper");
        ingredient2.setCategory(IngredientCategory.SPICE);

        // Setup shopping list
        shoppingList = new ShoppingList();
        shoppingList.setId(shoppingListId);
        shoppingList.setUser(user);
        shoppingList.setIngredients(new HashSet<>(Arrays.asList(ingredient1, ingredient2)));
        shoppingList.setStatus(ShoppingStatus.ACTIVE);
        shoppingList.setCreatedAt(now);
        shoppingList.setUpdatedAt(now);

        // Setup user profile DTO
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(userId);
        userProfileDto.setEmail("user@example.com");
        userProfileDto.setFirstName("Test");
        userProfileDto.setLastName("User");

        // Setup ingredient brief DTOs
        IngredientBriefDto ingredientBriefDto1 = new IngredientBriefDto();
        ingredientBriefDto1.setId(ingredientId1);
        ingredientBriefDto1.setName("Salt");
        ingredientBriefDto1.setCategory(IngredientCategory.SPICE);

        IngredientBriefDto ingredientBriefDto2 = new IngredientBriefDto();
        ingredientBriefDto2.setId(ingredientId2);
        ingredientBriefDto2.setName("Pepper");
        ingredientBriefDto2.setCategory(IngredientCategory.SPICE);

        // Setup shopping list DTO
        shoppingListDto = new ShoppingListDto();
        shoppingListDto.setId(shoppingListId);
        shoppingListDto.setUser(userProfileDto);
        shoppingListDto.setIngredients(new HashSet<>(Arrays.asList(ingredientBriefDto1, ingredientBriefDto2)));
        shoppingListDto.setStatus(ShoppingStatus.ACTIVE);
        shoppingListDto.setCreatedAt(now);
        shoppingListDto.setUpdatedAt(now);

        // Setup create shopping list DTO
        createShoppingListDto = new CreateShoppingListDto();
        createShoppingListDto.setIngredientIds(new HashSet<>(Arrays.asList(ingredientId1, ingredientId2)));

        // Setup update shopping list DTO
        updateShoppingListDto = new UpdateShoppingListDto();
        updateShoppingListDto.setIngredientIds(new HashSet<>(Collections.singletonList(ingredientId1)));
        updateShoppingListDto.setStatus(ShoppingStatus.COMPLETED);
    }

    @Test
    void getUserShoppingLists_Success() {
        // Arrange
        List<ShoppingList> shoppingLists = List.of(shoppingList);
        List<ShoppingListDto> shoppingListDtos = List.of(shoppingListDto);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(shoppingLists);
        when(shoppingListMapper.shoppingListsToShoppingListDtos(shoppingLists)).thenReturn(shoppingListDtos);

        // Act
        List<ShoppingListDto> result = shoppingListService.getUserShoppingLists("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(shoppingListDto, result.getFirst());

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByUserOrderByCreatedAtDesc(user);
        verify(shoppingListMapper).shoppingListsToShoppingListDtos(shoppingLists);
    }

    @Test
    void getShoppingListById_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.getShoppingListById(shoppingListId, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);
    }

    @Test
    void getShoppingListById_NotFound() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> shoppingListService.getShoppingListById(shoppingListId, "user@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Shopping list not found with id: " + shoppingListId, exception.getReason());

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verifyNoInteractions(shoppingListMapper);
    }

    @Test
    void createShoppingList_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findById(ingredientId2)).thenReturn(Optional.of(ingredient2));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.createShoppingList(createShoppingListDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(ingredientRepository).findById(ingredientId1);
        verify(ingredientRepository).findById(ingredientId2);
        verify(shoppingListRepository).save(any(ShoppingList.class));
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);
    }

    @Test
    void updateShoppingList_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));
        when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
        when(shoppingListRepository.save(shoppingList)).thenReturn(shoppingList);
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.updateShoppingList(shoppingListId, updateShoppingListDto, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(ingredientRepository).findById(ingredientId1);
        verify(shoppingListRepository).save(shoppingList);
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);

        assertEquals(ShoppingStatus.COMPLETED, shoppingList.getStatus());
        assertEquals(1, shoppingList.getIngredients().size());
        assertTrue(shoppingList.getIngredients().contains(ingredient1));
    }

    @Test
    void updateShoppingListStatus_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(shoppingList)).thenReturn(shoppingList);
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.updateShoppingListStatus(shoppingListId, ShoppingStatus.COMPLETED, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(shoppingListRepository).save(shoppingList);
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);

        assertEquals(ShoppingStatus.COMPLETED, shoppingList.getStatus());
    }

    @Test
    void deleteShoppingList_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));

        // Act
        shoppingListService.deleteShoppingList(shoppingListId, "user@example.com");

        // Assert
        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(shoppingListRepository).delete(shoppingList);
    }

    @Test
    void addIngredientsToShoppingList_Success() {
        // Arrange
        UUID newIngredientId = UUID.randomUUID();
        Ingredient newIngredient = new Ingredient();
        newIngredient.setId(newIngredientId);
        newIngredient.setName("Sugar");

        Set<UUID> ingredientIdsToAdd = new HashSet<>(Collections.singletonList(newIngredientId));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));
        when(ingredientRepository.findById(newIngredientId)).thenReturn(Optional.of(newIngredient));
        when(shoppingListRepository.save(shoppingList)).thenReturn(shoppingList);
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.addIngredientsToShoppingList(shoppingListId, ingredientIdsToAdd, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(ingredientRepository).findById(newIngredientId);
        verify(shoppingListRepository).save(shoppingList);
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);

        assertEquals(3, shoppingList.getIngredients().size());
        assertTrue(shoppingList.getIngredients().contains(newIngredient));
    }

    @Test
    void removeIngredientFromShoppingList_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));
        when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
        when(shoppingListRepository.save(shoppingList)).thenReturn(shoppingList);
        when(shoppingListMapper.shoppingListToShoppingListDto(shoppingList)).thenReturn(shoppingListDto);

        // Act
        ShoppingListDto result = shoppingListService.removeIngredientFromShoppingList(shoppingListId, ingredientId1, "user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(shoppingListDto, result);

        verify(userRepository).findByEmail("user@example.com");
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
        verify(ingredientRepository).findById(ingredientId1);
        verify(shoppingListRepository).save(shoppingList);
        verify(shoppingListMapper).shoppingListToShoppingListDto(shoppingList);

        assertEquals(1, shoppingList.getIngredients().size());
        assertFalse(shoppingList.getIngredients().contains(ingredient1));
        assertTrue(shoppingList.getIngredients().contains(ingredient2));
    }

    @Test
    void findUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Act
        User result = shoppingListService.findUserByEmail("user@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void findUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> shoppingListService.findUserByEmail("nonexistent@example.com"));

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void findShoppingListByIdAndUser_Success() {
        // Arrange
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.of(shoppingList));

        // Act
        ShoppingList result = shoppingListService.findShoppingListByIdAndUser(shoppingListId, user);

        // Assert
        assertNotNull(result);
        assertEquals(shoppingList, result);
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
    }

    @Test
    void findShoppingListByIdAndUser_NotFound() {
        // Arrange
        when(shoppingListRepository.findByIdAndUser(shoppingListId, user)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> shoppingListService.findShoppingListByIdAndUser(shoppingListId, user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Shopping list not found with id: " + shoppingListId, exception.getReason());
        verify(shoppingListRepository).findByIdAndUser(shoppingListId, user);
    }

    @Test
    void findIngredientsByIds_Success() {
        // Arrange
        Set<UUID> ingredientIds = new HashSet<>(Arrays.asList(ingredientId1, ingredientId2));

        when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findById(ingredientId2)).thenReturn(Optional.of(ingredient2));

        // Act
        Set<Ingredient> result = shoppingListService.findIngredientsByIds(ingredientIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(ingredient1));
        assertTrue(result.contains(ingredient2));

        verify(ingredientRepository).findById(ingredientId1);
        verify(ingredientRepository).findById(ingredientId2);
    }

    @Test
    void findIngredientsByIds_IngredientNotFound() {
        // Arrange
        Set<UUID> ingredientIds = new HashSet<>(Arrays.asList(ingredientId1, UUID.randomUUID()));

        when(ingredientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> shoppingListService.findIngredientsByIds(ingredientIds));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason()).startsWith("Ingredient not found with id: "));

        verify(ingredientRepository, atLeastOnce()).findById(any(UUID.class));
    }
}