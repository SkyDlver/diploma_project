package com.example.kooking.service;

import com.example.kooking.dto.LoginDto;
import com.example.kooking.dto.RegisterDto;
import com.example.kooking.dto.UserProfileDto;

public interface AuthService {
    UserProfileDto registerUser(RegisterDto registerDto);
    String loginUser(LoginDto loginDto);
}
