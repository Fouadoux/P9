package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.service.AppUserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AppUserResponseDto>> getAll() {
        log.info("GET /api/users/all - Récupération de tous les utilisateurs");
        List<AppUser> appUsers = appUserService.findAll();
        List<AppUserResponseDto> appUserResponseDtoList = appUserService.convertToDTOList(appUsers);
        log.info("Nombre d'utilisateurs récupérés : {}", appUserResponseDtoList.size());
        return ResponseEntity.ok(appUserResponseDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDto> getById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Récupération de l'utilisateur", id);
        AppUser user = appUserService.findById(id);
        AppUserResponseDto appUserResponseDto = appUserService.convertToDTO(user);
        log.info("Utilisateur récupéré : {}", appUserResponseDto);
        return ResponseEntity.ok(appUserResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public ResponseEntity<AppUserResponseDto> updateUser(@RequestBody @Valid AppUserResponseDto appUserResponseDto) {
        log.info("POST /api/users/update - Mise à jour de l'utilisateur : {}", appUserResponseDto);
        AppUserResponseDto userUpDate = appUserService.updateAppUser(appUserResponseDto);
        log.info("Utilisateur mis à jour : {}", userUpDate);
        return ResponseEntity.ok(userUpDate);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/toggle/{id}")
    public ResponseEntity<AppUserResponseDto> togglePatient(@PathVariable Long id) {
        log.info("PUT /api/users/deactivate/{} - Activation/Désactivation de l'utilisateur", id);
        AppUserResponseDto dto = appUserService.toggleActiveUser(id);
        log.info("Nouvel état de l'utilisateur : {}", dto);
        return ResponseEntity.ok().body(dto);
    }
}
