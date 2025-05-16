package com.glucovion.authservice.integration;

import com.glucovion.authservice.controller.AppUserController;
import com.glucovion.authservice.dto.AppUserResponseDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.glucovion.authservice.model.AppRole.ADMIN;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppUserController.class)
@Import(TestSecurityConfig.class)
class AppUserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAll_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetById_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        mockMvc.perform(post("/api/users/update")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testTogglePatient_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/users/toggle/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetById_WithAdminRole_ShouldReturnOk() throws Exception {
        Long id = 1L;
        AppUser appUser = new AppUser();
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        appUserResponseDto.setId(1L);
        appUserResponseDto.setFirstName("John");
        appUserResponseDto.setLastName("Doe");
        appUserResponseDto.setEmail("john.doe@example.com");
        appUserResponseDto.setRole(ADMIN);
        appUserResponseDto.setActive(true);

        when(appUserService.findById(id)).thenReturn(appUser);
        when(appUserService.convertToDTO(appUser)).thenReturn(appUserResponseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}")); // Ajustez selon le contenu attendu
    }

}
