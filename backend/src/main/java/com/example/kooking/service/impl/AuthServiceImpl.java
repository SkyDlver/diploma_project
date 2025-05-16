package com.example.kooking.service.impl;

import com.example.kooking.config.JwtService;
import com.example.kooking.dto.LoginDto;
import com.example.kooking.dto.RegisterDto;
import com.example.kooking.dto.UserProfileDto;
import com.example.kooking.model.User;
import com.example.kooking.model.UserPreferences;
import com.example.kooking.repository.UserPreferencesRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.service.AuthService;
import com.example.kooking.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserProfileDto registerUser(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use!");
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match!");
        }

        User user = userMapper.registerDtoToUser(registerDto);
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("USER"));
        }

       User savedUser = userRepository.save(user);

        UserPreferences preferences = UserPreferences.builder()
                .user(savedUser)
                .preferredMealTypes(new HashSet<>())
                .preferredCuisine ( new HashSet<>())
                .dietaryRestrictions(new HashSet<>())
                .preferredCookingMethods(new HashSet<>())
                .build();

        userPreferencesRepository.save(preferences);

        return userMapper.userToUserProfileDto(savedUser);
    }


    @Override
    public String loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!"));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials!");
        }

        return jwtService.generateToken(user.getEmail());
    }

}
