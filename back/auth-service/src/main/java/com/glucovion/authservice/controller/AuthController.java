package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;
import com.glucovion.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for user authentication and registration.
 * <p>
 * Exposes public endpoints to:
 * <ul>
 *   <li>Register new users</li>
 *   <li>Authenticate users and issue JWT tokens</li>
 * </ul>
 * <p>
 * This controller is publicly accessible and does not require prior authentication.
 */
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user and returns a JWT token upon success.
     *
     * @param userRegisterDto the user registration data
     * @return JWT token if registration is successful
     */
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody @Valid AppUserRegisterDto userRegisterDto) {
        log.info("[POST] Registering new user: {}", userRegisterDto.getEmail());
        AuthResponseDto jwt = authService.register(userRegisterDto);
        log.info("✅ User registered and token generated");
        return ResponseEntity.ok(jwt);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequestDto the user's login credentials
     * @return JWT token if authentication succeeds
     */
    @Operation(summary = "Authenticate a user and return a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto) {
        log.info("[POST] Login attempt for user: {}", loginRequestDto.getEmail());
        AuthResponseDto jwt = authService.login(loginRequestDto);
        log.info("✅ User authenticated and token issued");
        return ResponseEntity.ok(jwt);
    }
}
