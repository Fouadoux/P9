package com.glucovion.authservice.dto;

import com.glucovion.authservice.entity.AppRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String email;
    private AppRole role;
}