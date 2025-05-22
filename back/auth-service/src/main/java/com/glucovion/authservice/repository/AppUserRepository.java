package com.glucovion.authservice.repository;

import com.glucovion.authservice.model.AppUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link AppUser} entities.
 * <p>
 * Provides database access methods for user authentication and registration logic.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param username the email used as username
     * @return an optional containing the user, if found
     */
    Optional<AppUser> findByEmail(String username);

    /**
     * Checks if an account with the given email already exists.
     *
     * @param email the email to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(@Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email);
    Page<AppUser> findByLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase (
            String nameQuery,
            String emailQuery,
            Pageable pageable
    );


}
