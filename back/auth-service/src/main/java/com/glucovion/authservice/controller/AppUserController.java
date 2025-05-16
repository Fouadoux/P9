package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that handles operations related to application users.
 * <p>
 * This controller is restricted to administrators and allows them to:
 * <ul>
 *   <li>Retrieve all users</li>
 *   <li>Get user details by ID</li>
 *   <li>Update user information</li>
 *   <li>Toggle user active status</li>
 * </ul>
 * <p>
 * All actions are logged and secured with role-based access.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing application users (Admin only)")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Retrieves all registered users.
     *
     * @return list of users (DTOs)
     */
    @Operation(summary = "Get all registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AppUserResponseDto>> getAll() {
        log.info("[GET] Retrieving all users");
        List<AppUser> appUsers = appUserService.findAll();
        List<AppUserResponseDto> appUserResponseDtoList = appUserService.convertToDTOList(appUsers);
        log.info("✅ {} user(s) retrieved", appUserResponseDtoList.size());
        return ResponseEntity.ok(appUserResponseDtoList);
    }

    /**
     * Retrieves user details by user ID.
     *
     * @param id the user ID
     * @return user information
     */
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDto> getById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("[GET] Retrieving user with ID: {}", id);
        AppUser user = appUserService.findById(id);
        AppUserResponseDto appUserResponseDto = appUserService.convertToDTO(user);
        log.info("✅ User found: {}", appUserResponseDto);
        return ResponseEntity.ok(appUserResponseDto);
    }

    /**
     * Updates an existing user's information.
     *
     * @param appUserResponseDto the user data to update
     * @return updated user information
     */
    @Operation(summary = "Update user information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public ResponseEntity<AppUserResponseDto> updateUser(
            @RequestBody @Valid AppUserResponseDto appUserResponseDto) {
        log.info("[POST] Updating user: {}", appUserResponseDto);
        AppUserResponseDto updatedUser = appUserService.updateAppUser(appUserResponseDto);
        log.info("✅ User updated: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Toggles a user's active status.
     *
     * @param id the user ID
     * @return updated user with new status
     */
    @Operation(summary = "Toggle user's active status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User status toggled"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle/{id}")
    public ResponseEntity<AppUserResponseDto> togglePatient(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.info("[PUT] Toggling active status for user ID: {}", id);
        AppUserResponseDto dto = appUserService.toggleActiveUser(id);
        log.info("✅ User status updated: {}", dto);
        return ResponseEntity.ok().body(dto);
    }
}
