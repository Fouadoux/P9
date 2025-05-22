package com.glucovion.authservice.controller;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


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

    @Test
    void testSearchByLastName() {
        // Arrange
        String query = "john";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<AppUserResponseDto> dtoList = Arrays.asList(new AppUserResponseDto(), new AppUserResponseDto());
        Page<AppUserResponseDto> dtoPage = new PageImpl<>(dtoList, pageable, dtoList.size());

        when(appUserService.searchUsersPaginated(eq(query), any(Pageable.class))).thenReturn(dtoPage);

        // Act
        ResponseEntity<Page<AppUserResponseDto>> response = appUserController.searchByLastName(query, page, size);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dtoPage, response.getBody());
        verify(appUserService, times(1)).searchUsersPaginated(eq(query), any(Pageable.class));
    }

    @Test
    void testGetAllPaginated() {
        // Arrange
        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        List<AppUserResponseDto> dtoList = Arrays.asList(new AppUserResponseDto());
        Page<AppUserResponseDto> dtoPage = new PageImpl<>(dtoList, pageable, 1);

        when(appUserService.findAllPaginated(any(Pageable.class))).thenReturn(dtoPage);

        // Act
        ResponseEntity<Page<AppUserResponseDto>> response = appUserController.getAllPaginated(page, size);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dtoPage, response.getBody());
        verify(appUserService, times(1)).findAllPaginated(any(Pageable.class));
    }


}
