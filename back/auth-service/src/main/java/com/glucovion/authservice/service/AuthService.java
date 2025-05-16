package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;

/**
 * Service interface for handling user authentication and registration.
 * <p>
 * Defines operations to:
 * <ul>
 *   <li>Register a new user</li>
 *   <li>Authenticate an existing user and return a JWT token</li>
 * </ul>
 */
public interface AuthService {

    /**
     * Registers a new user and returns an authentication response with a JWT token.
     *
     * @param registerDto the registration request data
     * @return a response containing the JWT token and user details
     */
    AuthResponseDto register(AppUserRegisterDto registerDto);

    /**
     * Authenticates a user based on login credentials.
     *
     * @param loginRequest the login credentials
     * @return a response containing the JWT token and user details
     */
    AuthResponseDto login(LoginRequestDto loginRequest);
}
