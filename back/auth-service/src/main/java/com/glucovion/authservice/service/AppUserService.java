package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserService {

    Optional<AppUser> findByEmail(String email);
    AppUser save(AppUser appUser);
    List<AppUser> findAll();
    AppUserResponseDto convertToDTO(AppUser appUser);
    AppUser convertToAppUser(AppUserResponseDto appUserResponseDto);
    List<AppUserResponseDto> convertToDTOList(List<AppUser> appUsers);
    AppUserResponseDto updateAppUser(AppUserResponseDto appUserResponseDto);
    AppUser findById(Long id);
    AppUserResponseDto toggleActiveUser(Long id);


    }
