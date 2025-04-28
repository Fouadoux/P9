package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AppUserControllerTest {

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private AppUserController appUserController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        // Arrange
        List<AppUser> appUsers = Arrays.asList(new AppUser(), new AppUser());
        List<AppUserResponseDto> appUserResponseDtoList = Arrays.asList(new AppUserResponseDto(), new AppUserResponseDto());

        when(appUserService.findAll()).thenReturn(appUsers);
        when(appUserService.convertToDTOList(appUsers)).thenReturn(appUserResponseDtoList);

        // Act
        ResponseEntity<List<AppUserResponseDto>> response = appUserController.getAll();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(appUserResponseDtoList, response.getBody());
    }

    @Test
    void testGetById() {
        // Arrange
        Long id = 1L;
        AppUser appUser = new AppUser();
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();

        when(appUserService.findById(id)).thenReturn(appUser);
        when(appUserService.convertToDTO(appUser)).thenReturn(appUserResponseDto);

        // Act
        ResponseEntity<AppUserResponseDto> response = appUserController.getById(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(appUserResponseDto, response.getBody());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        AppUserResponseDto updatedAppUserResponseDto = new AppUserResponseDto();

        when(appUserService.updateAppUser(appUserResponseDto)).thenReturn(updatedAppUserResponseDto);

        // Act
        ResponseEntity<AppUserResponseDto> response = appUserController.updateUser(appUserResponseDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedAppUserResponseDto, response.getBody());
    }

    @Test
    void testTogglePatient() {
        // Arrange
        Long id = 1L;
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();

        when(appUserService.toggleActiveUser(id)).thenReturn(appUserResponseDto);

        // Act
        ResponseEntity<AppUserResponseDto> response = appUserController.togglePatient(id);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(appUserResponseDto, response.getBody());
    }
}
