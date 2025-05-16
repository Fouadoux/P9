package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.exception.DisabledAccountException;
import com.glucovion.authservice.repository.AppUserRepository;
import com.glucovion.authservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.glucovion.authservice.model.AppRole.PENDING;

/**
 * Implementation of {@link AuthService} that handles user registration and login.
 * <p>
 * Generates JWT tokens for authenticated users and ensures account validity.
 * </p>
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AppUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new user with PENDING role and encodes their password.
     * A JWT token is returned if registration is successful.
     *
     * @param registerDto the registration data
     * @return the authentication response containing JWT and user info
     * @throws IllegalArgumentException if the email is already used
     */
    @Override
    public AuthResponseDto register(AppUserRegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(registerDto.getEmail());
        appUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        appUser.setFirstName(registerDto.getFirstName());
        appUser.setLastName(registerDto.getLastName());
        appUser.setRole(PENDING);
        appUser.setActive(true);

        AppUser savedUser = userRepository.save(appUser);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDto(
                token,
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    /**
     * Authenticates a user by verifying email, password, and active status.
     * Returns a JWT token if successful.
     *
     * @param loginRequest the login credentials
     * @return the authentication response containing JWT and user info
     * @throws IllegalArgumentException if email is not found
     * @throws BadCredentialsException  if password is invalid
     * @throws DisabledAccountException if the account is deactivated
     */
    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        AppUser user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (!user.getActive()) {
            throw new DisabledAccountException("Ce compte est désactivé. Contacte un admin.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
