package com.foodly.identity.application.service;

import com.foodly.identity.application.dto.AuthResponseDto;
import com.foodly.identity.application.dto.LoginRequestDto;
import com.foodly.identity.application.dto.RegisterRequestDto;
import com.foodly.identity.application.dto.UserProfileDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    UserProfileDto getUserProfile(String userId);
}
