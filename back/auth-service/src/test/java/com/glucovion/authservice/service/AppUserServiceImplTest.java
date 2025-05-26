package com.glucovion.authservice.service;

import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.model.AppRole;
import com.glucovion.authservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.glucovion.authservice.model.AppRole.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUser sampleUser;
    private AppUserResponseDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleUser = new AppUser();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@example.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setRole(USER);
        sampleUser.setActive(true);

        sampleDto = new AppUserResponseDto();
        sampleDto.setId(1L);
        sampleDto.setEmail("test@example.com");
        sampleDto.setFirstName("John");
        sampleDto.setLastName("Doe");
        sampleDto.setRole(USER);
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
        assertFalse(result.getActive());
    }

    // Tests pour toggleActiveUser()
    @Test
    void toggleActiveUser_ShouldFlipActiveStatus() {
        sampleUser.setActive(true);
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUserResponseDto result = appUserService.toggleActiveUser(1L);

        assertFalse(result.getActive()); // Vérifie le basculement true -> false

        // Second appel pour tester false -> true
        result = appUserService.toggleActiveUser(1L);
        assertTrue(result.getActive());
    }

    @Test
    void searchUsersPaginated_ShouldReturnMatchingUsersAsDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AppUser> users = List.of(sampleUser);
        Page<AppUser> page = new PageImpl<>(users, pageable, 1);

        when(appUserRepository.findByLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "test", "test", pageable)).thenReturn(page);

        Page<AppUserResponseDto> result = appUserService.searchUsersPaginated("test", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("test@example.com", result.getContent().get(0).getEmail());
        verify(appUserRepository, times(1))
                .findByLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test", pageable);
    }

    @Test
    void findAllPaginated_ShouldReturnAllUsersAsDTOs() {
        Pageable pageable = PageRequest.of(0, 5);
        List<AppUser> users = List.of(sampleUser);
        Page<AppUser> page = new PageImpl<>(users, pageable, 1);

        when(appUserRepository.findAll(pageable)).thenReturn(page);

        Page<AppUserResponseDto> result = appUserService.findAllPaginated(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("test@example.com", result.getContent().get(0).getEmail());
        verify(appUserRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchUsersPaginated_ShouldReturnEmptyPage_WhenNoMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(appUserRepository.findByLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "inconnu", "inconnu", pageable)).thenReturn(emptyPage);

        Page<AppUserResponseDto> result = appUserService.searchUsersPaginated("inconnu", pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldUpdateUserWithNewPassword() {
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setPassword("oldEncoded");

        AppUserResponseDto dto = new AppUserResponseDto();
        dto.setId(1L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice@example.com");
        dto.setRole(USER);
        dto.setActive(true);
        dto.setPassword("newPlainPassword");

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPlainPassword")).thenReturn("encodedPassword");
        when(appUserRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AppUserResponseDto result = appUserService.updateAppUser(dto);

        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("alice@example.com", result.getEmail());
        verify(passwordEncoder).encode("newPlainPassword");
        verify(appUserRepository).save(existingUser);
        assertEquals("encodedPassword", existingUser.getPassword());
    }

    @Test
    void shouldNotUpdatePasswordIfBlank() {
        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setPassword("alreadyEncoded");

        AppUserResponseDto dto = new AppUserResponseDto();
        dto.setId(1L);
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setEmail("alice@example.com");
        dto.setRole(USER);
        dto.setActive(true);
        dto.setPassword(""); // Intentionnellement vide

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(appUserRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AppUserResponseDto result = appUserService.updateAppUser(dto);

        assertEquals("alreadyEncoded", existingUser.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }



}