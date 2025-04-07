package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;


public interface AuthService {
    AuthResponseDto register(AppUserRegisterDto registerDto);
    AuthResponseDto login(LoginRequestDto loginRequest);
}
