package com.example.kooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 20, message = "firstName must be between 3 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 20, message = "lastName must be between 3 and 20 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size(min = 3, max = 90, message = "email must be between 3 and 20 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Please confirm your password")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters long")
    private String confirmPassword;
}
