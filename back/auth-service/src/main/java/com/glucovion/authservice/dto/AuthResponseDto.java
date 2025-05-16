package com.glucovion.authservice.dto;

import com.glucovion.authservice.model.AppRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO returned after successful authentication (login or registration).
 */
@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT and user information")
public class AuthResponseDto {

    @Schema(description = "JWT access token for the authenticated user", example = "eyJhbGciOiJIUzI1NiIsInR...")
    private String token;

    @Schema(description = "Authenticated user's first name", example = "John")
    private String firstName;

    @Schema(description = "Authenticated user's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Authenticated user's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Role assigned to the user", example = "USER")
    private AppRole role;
}
