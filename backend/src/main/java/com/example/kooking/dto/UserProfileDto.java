package com.example.kooking.dto;

import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
