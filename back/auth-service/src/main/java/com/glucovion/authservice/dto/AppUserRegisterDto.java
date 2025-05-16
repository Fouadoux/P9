package com.glucovion.authservice.dto;

import com.glucovion.authservice.validation.StrictEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used for registering a new user in the system.
 */
@Data
@Schema(description = "Data required to register a new user")
public class AppUserRegisterDto {

    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;

    @StrictEmail
    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password (plain text, will be encrypted)", example = "P@ssw0rd123", required = true)
    private String password;
}
