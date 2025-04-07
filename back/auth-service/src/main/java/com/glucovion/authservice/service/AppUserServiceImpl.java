package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public AppUserResponseDto updateAppUser(AppUserResponseDto appUserResponseDto) {
        AppUser appUser=appUserRepository.findById(appUserResponseDto.getId()).orElseThrow(()->
                new UsernameNotFoundException("user not found"));

        AppUser userSave= appUserRepository.save(appUser);
        return convertToDTO(userSave);
    }
}
