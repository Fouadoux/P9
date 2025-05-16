package com.glucovion.authservice.dto;

import com.glucovion.authservice.validation.StrictEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used for user login requests.
 */
@Data
@Schema(description = "Credentials required to authenticate a user")
public class LoginRequestDto {

    @StrictEmail(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "P@ssw0rd123", required = true)
    private String password;
}
