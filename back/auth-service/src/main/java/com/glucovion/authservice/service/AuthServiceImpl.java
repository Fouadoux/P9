package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.dto.AuthResponseDto;
import com.glucovion.authservice.dto.LoginRequestDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.repository.AppUserRepository;
import com.glucovion.authservice.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.glucovion.authservice.entity.AppRole.PENDING;
import static com.glucovion.authservice.entity.AppRole.USER;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AppUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final JwtService jwtService;



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

        return new AuthResponseDto(token, savedUser.getEmail(), savedUser.getRole());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        AppUser user=userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDto(token, user.getEmail(), user.getRole());
    }


}
