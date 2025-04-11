package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    public AppUser findById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUserResponseDto convertToDTO(AppUser appUser) {

        AppUserResponseDto appUserResponseDto=new AppUserResponseDto();
        appUserResponseDto.setId(appUser.getId());
        appUserResponseDto.setEmail(appUser.getEmail());
        appUserResponseDto.setFirstName(appUser.getFirstName());
        appUserResponseDto.setLastName(appUser.getLastName());
        appUserResponseDto.setRole(appUser.getRole());
        appUserResponseDto.setActive(appUser.isActive());
        return appUserResponseDto;
    }

    public AppUser convertToAppUser(AppUserResponseDto appUserResponseDto) {
        AppUser appUser=new AppUser();
        appUser.setEmail(appUserResponseDto.getEmail());
        appUser.setFirstName(appUserResponseDto.getFirstName());
        appUser.setLastName(appUserResponseDto.getLastName());
        appUser.setRole(appUserResponseDto.getRole());
        appUser.setActive(appUserResponseDto.isActive());
        return appUser;
    }

    public List<AppUserResponseDto> convertToDTOList(List<AppUser> appUsers) {
        List<AppUserResponseDto> appUserResponseDtoList=new ArrayList<>();
        for (AppUser appUser : appUsers) {
            appUserResponseDtoList.add(convertToDTO(appUser));
        }
        return appUserResponseDtoList;
    }

    public AppUserResponseDto updateAppUser(AppUserResponseDto dto) {
        AppUser appUser = appUserRepository.findById(dto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // 🔧 Mise à jour des champs
        appUser.setFirstName(dto.getFirstName());
        appUser.setLastName(dto.getLastName());
        appUser.setEmail(dto.getEmail());
        appUser.setRole(dto.getRole());
        appUser.setActive(dto.isActive());

        AppUser updatedUser = appUserRepository.save(appUser);
        return convertToDTO(updatedUser);
    }

    public AppUserResponseDto toggleActiveUser(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        log.info("status: {}", !appUser.isActive());
        appUser.setActive(!appUser.isActive());
        log.info("status: {}", !appUser.isActive());
        AppUser updatedUser = appUserRepository.save(appUser);
        return convertToDTO(updatedUser);
    }



}
