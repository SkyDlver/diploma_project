package com.example.kooking.service.impl;

import com.example.kooking.config.JwtService;
import com.example.kooking.dto.LoginDto;
import com.example.kooking.dto.RegisterDto;
import com.example.kooking.dto.UserProfileDto;
import com.example.kooking.model.User;
import com.example.kooking.model.UserPreferences;
import com.example.kooking.repository.UserPreferencesRepository;
import com.example.kooking.repository.UserRepository;
import com.example.kooking.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterDto validRegisterDto;
    private User user;
    private UserProfileDto userProfileDto;
    private LoginDto loginDto;
    private final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    void setUp() {
        // Setup valid registration data
        validRegisterDto = new RegisterDto();
        validRegisterDto.setEmail("test@example.com");
        validRegisterDto.setPassword("password123");
        validRegisterDto.setConfirmPassword("password123");
        validRegisterDto.setFirstName("Test");
        validRegisterDto.setLastName("User");

        // Setup user
        user = new User();
        user.setId(TEST_UUID);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRoles(Set.of("USER"));

        // Setup user profile DTO
        userProfileDto = new UserProfileDto();
        userProfileDto.setId(TEST_UUID);
        userProfileDto.setEmail("test@example.com");
        userProfileDto.setFirstName("Test");
        userProfileDto.setLastName("User");

        // Setup login DTO
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.registerDtoToUser(any(RegisterDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserProfileDto(any(User.class))).thenReturn(userProfileDto);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        UserProfileDto result = authService.registerUser(validRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals(userProfileDto.getId(), result.getId());
        assertEquals(userProfileDto.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(validRegisterDto.getEmail());
        verify(userMapper).registerDtoToUser(validRegisterDto);
        verify(passwordEncoder).encode(validRegisterDto.getPassword());
        verify(userRepository).save(user);
        verify(userPreferencesRepository).save(any(UserPreferences.class));
        verify(userMapper).userToUserProfileDto(user);
    }

    @Test
    void registerUser_EmailAlreadyInUse() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.registerUser(validRegisterDto));

        assertEquals("400 BAD_REQUEST \"Email already in use!\"", exception.getMessage());
        verify(userRepository).findByEmail(validRegisterDto.getEmail());
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository, userPreferencesRepository);
    }

    @Test
    void registerUser_PasswordsDoNotMatch() {
        // Arrange
        RegisterDto invalidDto = new RegisterDto();
        invalidDto.setEmail("test@example.com");
        invalidDto.setPassword("password123");
        invalidDto.setConfirmPassword("differentPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.registerUser(invalidDto));

        assertEquals("400 BAD_REQUEST \"Passwords do not match!\"", exception.getMessage());
        verify(userRepository).findByEmail(invalidDto.getEmail());
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository, userPreferencesRepository);
    }

    @Test
    void registerUser_SetsDefaultRolesWhenNull() {
        // Arrange
        User userWithNullRoles = new User();
        userWithNullRoles.setEmail("test@example.com");
        userWithNullRoles.setRoles(null);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.registerDtoToUser(any(RegisterDto.class))).thenReturn(userWithNullRoles);
        when(userRepository.save(any(User.class))).thenReturn(userWithNullRoles);
        when(userMapper.userToUserProfileDto(any(User.class))).thenReturn(userProfileDto);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        authService.registerUser(validRegisterDto);

        // Assert
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getRoles() != null && savedUser.getRoles().contains("USER")));
    }

    @Test
    void loginUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        String token = authService.loginUser(loginDto);

        // Assert
        assertNotNull(token);
        assertEquals("jwt-token", token);

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        verify(jwtService).generateToken(loginDto.getEmail());
    }

    @Test
    void loginUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginUser(loginDto));

        assertEquals("400 BAD_REQUEST \"User not found!\"", exception.getMessage());
        verify(userRepository).findByEmail(loginDto.getEmail());
        verifyNoInteractions(authenticationManager, jwtService);
    }

    @Test
    void loginUser_InvalidCredentials() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.loginUser(loginDto));

        assertEquals("401 UNAUTHORIZED \"Invalid login credentials!\"", exception.getMessage());
        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(jwtService);
    }
}