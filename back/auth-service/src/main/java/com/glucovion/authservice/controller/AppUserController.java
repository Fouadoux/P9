package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")

public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppUserResponseDto>> getAll() {
        List<AppUser> appUsers = appUserService.findAll();
        List<AppUserResponseDto> appUserResponseDtoList= appUserService.convertToDTOList(appUsers);
        return ResponseEntity.ok(appUserResponseDtoList);
    }

    @PostMapping("/update")
    public ResponseEntity<AppUserResponseDto> updateUser(@RequestBody @Valid AppUserResponseDto appUserResponseDto) {
        AppUserResponseDto userUpDate = appUserService.updateAppUser(appUserResponseDto);
        return ResponseEntity.ok(userUpDate);
    }



}
