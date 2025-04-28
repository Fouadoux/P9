package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.entity.AppUser;
import com.glucovion.authservice.entity.AppRole;
import com.glucovion.authservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    private AppUser sampleUser;
    private AppUserResponseDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleUser = new AppUser();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@example.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setRole(AppRole.USER);
        sampleUser.setActive(true);

        sampleDto = new AppUserResponseDto();
        sampleDto.setId(1L);
        sampleDto.setEmail("test@example.com");
        sampleDto.setFirstName("John");
        sampleDto.setLastName("Doe");
        sampleDto.setRole(AppRole.USER);
        sampleDto.setActive(true);
    }

    // Tests pour findByEmail()
    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        Optional<AppUser> result = appUserService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        when(appUserRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<AppUser> result = appUserService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }

    // Tests pour save()
    @Test
    void save_ShouldReturnSavedUser() {
        when(appUserRepository.save(any(AppUser.class))).thenReturn(sampleUser);

        AppUser savedUser = appUserService.save(sampleUser);

        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        verify(appUserRepository, times(1)).save(sampleUser);
    }

    // Tests pour findAll()
    @Test
    void findAll_ShouldReturnAllUsers() {
        List<AppUser> users = Arrays.asList(sampleUser, new AppUser());
        when(appUserRepository.findAll()).thenReturn(users);

        List<AppUser> result = appUserService.findAll();

        assertEquals(2, result.size());
        verify(appUserRepository, times(1)).findAll();
    }

    // Tests pour findById()
    @Test
    void findById_ShouldReturnUser_WhenExists() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        AppUser result = appUserService.findById(1L);

        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> appUserService.findById(99L));
    }

    // Tests pour convertToDTO()
    @Test
    void convertToDTO_ShouldMapCorrectly() {
        AppUserResponseDto dto = appUserService.convertToDTO(sampleUser);

        assertEquals(sampleUser.getId(), dto.getId());
        assertEquals(sampleUser.getEmail(), dto.getEmail());
        assertEquals(sampleUser.getRole(), dto.getRole());
    }

    // Tests pour convertToAppUser()
    @Test
    void convertToAppUser_ShouldMapCorrectly() {
        AppUser user = appUserService.convertToAppUser(sampleDto);

        assertEquals(sampleDto.getEmail(), user.getEmail());
        assertEquals(sampleDto.getRole(), user.getRole());
    }

    // Tests pour convertToDTOList()
    @Test
    void convertToDTOList_ShouldConvertAllItems() {
        List<AppUser> users = Arrays.asList(sampleUser, sampleUser);
        List<AppUserResponseDto> dtos = appUserService.convertToDTOList(users);

        assertEquals(2, dtos.size());
        assertEquals(sampleUser.getEmail(), dtos.get(0).getEmail());
    }

    // Tests pour updateAppUser()
    @Test
    void updateAppUser_ShouldUpdateFields() {
        AppUserResponseDto updateDto = new AppUserResponseDto();
        updateDto.setId(1L);
        updateDto.setFirstName("Updated");
        updateDto.setLastName("Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setRole(AppRole.ADMIN);
        updateDto.setActive(false);

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUserResponseDto result = appUserService.updateAppUser(updateDto);

        assertEquals("Updated", result.getFirstName());
        assertEquals("updated@example.com", result.getEmail());
        assertFalse(result.isActive());
    }

    // Tests pour toggleActiveUser()
    @Test
    void toggleActiveUser_ShouldFlipActiveStatus() {
        sampleUser.setActive(true);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUserResponseDto result = appUserService.toggleActiveUser(1L);

        assertFalse(result.isActive()); // Vérifie le basculement true -> false

        // Second appel pour tester false -> true
        result = appUserService.toggleActiveUser(1L);
        assertTrue(result.isActive());
    }
}