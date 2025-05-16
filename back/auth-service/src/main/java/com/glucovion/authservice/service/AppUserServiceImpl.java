package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link AppUserService} interface.
 * <p>
 * Handles user-related operations such as retrieval, update, conversion between entity and DTO,
 * and toggling the user's active status.
 * </p>
 */
@Log4j2
@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email
     * @return optional containing the user, if found
     */
    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    /**
     * Saves a new user or updates an existing user.
     *
     * @param appUser the user to save
     * @return the saved user entity
     */
    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    /**
     * Retrieves all users.
     *
     * @return list of all user entities
     */
    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return the user entity
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public AppUser findById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Converts an AppUser entity to a response DTO.
     *
     * @param appUser the user entity
     * @return the corresponding DTO
     */
    @Override
    public AppUserResponseDto convertToDTO(AppUser appUser) {
        AppUserResponseDto dto = new AppUserResponseDto();
        dto.setId(appUser.getId());
        dto.setEmail(appUser.getEmail());
        dto.setFirstName(appUser.getFirstName());
        dto.setLastName(appUser.getLastName());
        dto.setRole(appUser.getRole());
        dto.setActive(appUser.getActive());
        return dto;
    }

    /**
     * Converts a response DTO to an AppUser entity.
     *
     * @param dto the DTO to convert
     * @return the corresponding entity
     */
    @Override
    public AppUser convertToAppUser(AppUserResponseDto dto) {
        AppUser user = new AppUser();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole());
        user.setActive(dto.getActive());
        return user;
    }

    /**
     * Converts a list of AppUser entities to a list of DTOs.
     *
     * @param appUsers the list of user entities
     * @return the corresponding list of DTOs
     */
    @Override
    public List<AppUserResponseDto> convertToDTOList(List<AppUser> appUsers) {
        List<AppUserResponseDto> dtos = new ArrayList<>();
        for (AppUser user : appUsers) {
            dtos.add(convertToDTO(user));
        }
        return dtos;
    }

    /**
     * Updates a user's information based on a DTO.
     *
     * @param dto the updated user data
     * @return the updated user as DTO
     */
    @Override
    public AppUserResponseDto updateAppUser(AppUserResponseDto dto) {
        AppUser user = appUserRepository.findById(dto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setActive(dto.getActive());

        AppUser updated = appUserRepository.save(user);
        return convertToDTO(updated);
    }

    /**
     * Toggles the active status of a user.
     *
     * @param id the user's ID
     * @return the updated user as DTO
     */
    @Override
    public AppUserResponseDto toggleActiveUser(Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("Current status: {}", user.getActive());
        user.setActive(!user.getActive());
        log.info("New status: {}", user.getActive());

        AppUser updated = appUserRepository.save(user);
        return convertToDTO(updated);
    }
}
