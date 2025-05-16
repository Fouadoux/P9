package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.exception.DisabledAccountException;
import com.glucovion.authservice.repository.AppUserRepository;
import com.glucovion.authservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.glucovion.authservice.model.AppRole.PENDING;
import static com.glucovion.authservice.model.AppRole.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_ShouldReturnAuthResponse_WhenSuccessful() {
        // Arrange
        AppUserRegisterDto registerDto = new AppUserRegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");

        AppUser savedUser = new AppUser();
        savedUser.setEmail(registerDto.getEmail());
        savedUser.setFirstName(registerDto.getFirstName());
        savedUser.setLastName(registerDto.getLastName());
        savedUser.setRole(PENDING);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(AppUser.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("jwtToken");

        // Act
        AuthResponseDto response = authService.register(registerDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(PENDING, response.getRole());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        AppUserRegisterDto registerDto = new AppUserRegisterDto();
        registerDto.setEmail("existing@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDto));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        AppUser user = new AppUser();
        user.setEmail(loginRequest.getEmail());
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(USER);
        user.setActive(true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("jwtToken");

        // Act
        AuthResponseDto response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        AppUser user = new AppUser();
        user.setEmail(loginRequest.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenAccountDisabled() {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        AppUser user = new AppUser();
        user.setEmail(loginRequest.getEmail());
        user.setPassword("encodedPassword");
        user.setActive(false);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DisabledAccountException.class, () -> authService.login(loginRequest));
    }
}