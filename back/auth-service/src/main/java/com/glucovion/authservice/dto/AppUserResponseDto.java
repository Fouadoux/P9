package com.glucovion.authservice.dto;

import com.glucovion.authservice.entity.AppRole;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponseDto {
    private Long id;
    private String lastName;
    private String firstName;

    @Email(message = "Invalid email format")
    private String email;

    private AppRole role;
    private boolean active;
}
