package com.example.kooking.repository;

import com.example.kooking.model.ShoppingList;
import com.example.kooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {
    List<ShoppingList> findByUserOrderByCreatedAtDesc(User user);
    Optional<ShoppingList> findByIdAndUser(UUID id, User user);
}