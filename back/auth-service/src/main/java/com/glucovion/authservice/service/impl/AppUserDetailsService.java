package com.glucovion.authservice.service.impl;

import com.glucovion.authservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} used by Spring Security.
 * <p>
 * Loads user-specific data during the authentication process by email (used as username).
 * Returns an {@link org.springframework.security.core.userdetails.UserDetails} implementation (AppUser).
 * </p>
 *
 * <p>This class acts as the bridge between Spring Security and the user repository.</p>
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    /**
     * Loads a user from the database using their email.
     *
     * @param email the email used as the unique identifier
     * @return the user details
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
