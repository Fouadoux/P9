package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing application users.
 * <p>
 * Defines user-related operations such as retrieval, update, conversion,
 * and status toggling. Implemented by the business layer.
 * </p>
 */
public interface AppUserService {

    /**
     * Finds a user by their email.
     *
     * @param email the user's email
     * @return optional containing the user if found
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Persists a new user or updates an existing one.
     *
     * @param appUser the user entity to save
     * @return the saved user entity
     */
    AppUser save(AppUser appUser);

    /**
     * Retrieves all users from the database.
     *
     * @return list of all users
     */
    List<AppUser> findAll();

    /**
     * Converts an AppUser entity to a DTO.
     *
     * @param appUser the user entity
     * @return the corresponding DTO
     */
    AppUserResponseDto convertToDTO(AppUser appUser);

    /**
     * Converts a DTO to an AppUser entity.
     *
     * @param appUserResponseDto the DTO to convert
     * @return the corresponding entity
     */
    AppUser convertToAppUser(AppUserResponseDto appUserResponseDto);

    /**
     * Converts a list of AppUser entities to a list of DTOs.
     *
     * @param appUsers list of user entities
     * @return list of DTOs
     */
    List<AppUserResponseDto> convertToDTOList(List<AppUser> appUsers);

    /**
     * Updates an existing user's information.
     *
     * @param appUserResponseDto the updated user data
     * @return the updated user DTO
     */
    AppUserResponseDto updateAppUser(AppUserResponseDto appUserResponseDto);

    /**
     * Retrieves a user by ID.
     *
     * @param id the user's ID
     * @return the user entity
     */
    AppUser findById(Long id);

    /**
     * Toggles the active status of a user.
     *
     * @param id the user's ID
     * @return updated user DTO with new status
     */
    AppUserResponseDto toggleActiveUser(Long id);
}
