package com.example.kooking.dto;

import com.example.kooking.enums.ShoppingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShoppingStatusDto {
    @NotNull(message = "Status is required")
    private ShoppingStatus status;
}
