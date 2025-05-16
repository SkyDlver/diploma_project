package com.example.kooking.service.impl;

import com.example.kooking.dto.*;
import com.example.kooking.enums.ShoppingStatus;
import com.example.kooking.model.Ingredient;
import com.example.kooking.model.ShoppingList;
import com.example.kooking.model.User;
import com.example.kooking.repository.IngredientRepository;
import com.example.kooking.repository.ShoppingListRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.service.ShoppingListService;
import com.example.kooking.utils.ShoppingListMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final ShoppingListMapper shoppingListMapper;

    public List<ShoppingListDto> getUserShoppingLists(String userEmail) {
        User user = findUserByEmail(userEmail);
        List<ShoppingList> shoppingLists = shoppingListRepository.findByUserOrderByCreatedAtDesc(user);
        return shoppingListMapper.shoppingListsToShoppingListDtos(shoppingLists);
    }

    public ShoppingListDto getShoppingListById(UUID id, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);
        return shoppingListMapper.shoppingListToShoppingListDto(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto createShoppingList(CreateShoppingListDto createShoppingListDto, String userEmail) {
        User user = findUserByEmail(userEmail);
        Set<Ingredient> ingredients = findIngredientsByIds(createShoppingListDto.getIngredientIds());

        ShoppingList shoppingList = ShoppingList.builder()
                .user(user)
                .ingredients(ingredients)
                .status(ShoppingStatus.ACTIVE)
                .build();

        ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.shoppingListToShoppingListDto(savedShoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto updateShoppingList(UUID id, UpdateShoppingListDto updateShoppingListDto, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);

        if (updateShoppingListDto.getIngredientIds() != null) {
            Set<Ingredient> ingredients = findIngredientsByIds(updateShoppingListDto.getIngredientIds());
            shoppingList.setIngredients(ingredients);
        }

        if (updateShoppingListDto.getStatus() != null) {
            shoppingList.setStatus(updateShoppingListDto.getStatus());
        }

        ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.shoppingListToShoppingListDto(updatedShoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto updateShoppingListStatus(UUID id, ShoppingStatus status, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);

        shoppingList.setStatus(status);
        ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.shoppingListToShoppingListDto(updatedShoppingList);
    }

    @Override
    @Transactional
    public void deleteShoppingList(UUID id, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);
        shoppingListRepository.delete(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto addIngredientsToShoppingList(UUID id, Set<UUID> ingredientIds, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);

        Set<Ingredient> ingredientsToAdd = findIngredientsByIds(ingredientIds);

        // Add new ingredients to the existing set
        Set<Ingredient> updatedIngredients = new HashSet<>(shoppingList.getIngredients());
        updatedIngredients.addAll(ingredientsToAdd);
        shoppingList.setIngredients(updatedIngredients);

        ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.shoppingListToShoppingListDto(updatedShoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto removeIngredientFromShoppingList(UUID id, UUID ingredientId, String userEmail) {
        User user = findUserByEmail(userEmail);
        ShoppingList shoppingList = findShoppingListByIdAndUser(id, user);

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ingredient not found with id: " + id));
        // Remove the ingredient from the set
        shoppingList.getIngredients().remove(ingredient);

        ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.shoppingListToShoppingListDto(updatedShoppingList);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public ShoppingList findShoppingListByIdAndUser(UUID id, User user) {
        return shoppingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Shopping list not found with id: " + id));
    }

    @Override
    public Set<Ingredient> findIngredientsByIds(Set<UUID> ingredientIds) {
        return ingredientIds.stream()
                .map(id -> ingredientRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Ingredient not found with id: " + id)))
                .collect(Collectors.toSet());
    }
}
