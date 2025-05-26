package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.exception.DisabledAccountException;
import com.glucovion.authservice.repository.AppUserRepository;
import com.glucovion.authservice.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthServiceImpl implements AuthService {

    private AppUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto register(AppUserRegisterDto registerDto) {
        log.info("Tentative d'enregistrement pour l'email: {}", registerDto.getEmail());

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.warn("Échec de l'enregistrement : l'email {} est déjà utilisé.", registerDto.getEmail());
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
        log.info("Utilisateur enregistré avec succès : {}", savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);
        log.debug("JWT généré pour l'utilisateur {}", savedUser.getEmail());

        return new AuthResponseDto(
                token,
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        log.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

        AppUser user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Email non trouvé: {}", loginRequest.getEmail());
                    return new BadCredentialsException("INVALID_CREDENTIALS");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Mot de passe invalide pour l'email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("INVALID_PASSWORD");
        }

        if (!user.getActive()) {
            log.warn("Compte désactivé pour l'utilisateur: {}", loginRequest.getEmail());
            throw new DisabledAccountException("Ce compte est désactivé. Contacte un admin.");
        }

        String token = jwtService.generateToken(user);
        log.info("Connexion réussie pour l'utilisateur: {}", user.getEmail());
        log.debug("JWT généré pour l'utilisateur {}", user.getEmail());

        return new AuthResponseDto(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }

}
