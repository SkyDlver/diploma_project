package com.example.kooking.utils;

import com.example.kooking.dto.ShoppingListDto;
import com.example.kooking.model.ShoppingList;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, IngredientMapper.class})
public interface ShoppingListMapper {

    @Mapping(target = "user", source = "user")
    @Mapping(target = "ingredients", source = "ingredients")
    ShoppingListDto shoppingListToShoppingListDto(ShoppingList shoppingList);

    List<ShoppingListDto> shoppingListsToShoppingListDtos(List<ShoppingList> shoppingLists);
}