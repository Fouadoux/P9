package com.glucovion.authservice.dto;

import com.glucovion.authservice.model.AppRole;
import com.glucovion.authservice.model.AppUsersStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned when fetching user information (admin usage).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representation of an application user returned by the API")
public class AppUserResponseDto {

    @Schema(description = "User ID", example = "42")
    private Long id;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's first name", example = "Jane")
    private String firstName;

    @Schema(description = "User's email address", example = "jane.doe@example.com")
    private String email;

    @Schema(description = "User's pass word", example = "PassWord123^^")
    private String password;

    @Schema(description = "User's role (e.g., USER or ADMIN)", example = "USER")
    private AppRole role;

    @Schema(description = "Whether the user account is active", example = "true", defaultValue = "true")
    private Boolean active = true;

    public AppUsersStatus getStatus() {
        return active ? AppUsersStatus.ACTIF : AppUsersStatus.INACTIF;
    }
}
