package com.glucovion.authservice.dto;

import com.glucovion.authservice.entity.AppRole;
import com.glucovion.authservice.entity.AppUsersStatus;
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
    private String email;
    private AppRole role;
    private Boolean active=true;

    public AppUsersStatus getStatus() {
        return active ? AppUsersStatus.ACTIF : AppUsersStatus.INACTIF;
    }
}
