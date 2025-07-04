package com.glucovion.authservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucovion.authservice.dto.AppUserRegisterDto;
import com.glucovion.authservice.model.AppUser;
import com.glucovion.authservice.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.glucovion.authservice.model.AppRole.PENDING;
import static com.glucovion.authservice.model.AppRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccessfully() throws Exception {
        AppUserRegisterDto dto = new AppUserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setPassword("password123@");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("PENDING"));

        // Vérifie qu’il est bien en base H2
        AppUser user = appUserRepository.findByEmail("test@example.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getRole().name()).isEqualTo("PENDING");
        assertThat(user.getPassword()).doesNotContain("password123"); // Doit être encodé
    }

    @Test
    void testRegisterDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        // 1) Prépare déjà un user en base
        AppUserRegisterDto dto = new AppUserRegisterDto();
        dto.setEmail("dup@example.com");
        dto.setFirstName("Dup");
        dto.setLastName("User");
        dto.setPassword("password123@");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 2) On retente avec la même adresse
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ILLEGAL_ARGUMENT"))
                .andExpect(jsonPath("$.details").value("Email address already in use"));
    }

    @Test
    void testLoginSuccess_ShouldReturnJwt() throws Exception {
        // Crée d’abord le compte (via service ou endpoint)
        AppUserRegisterDto dto = new AppUserRegisterDto();
        dto.setEmail("login@example.com");
        dto.setFirstName("Log");
        dto.setLastName("In");
        dto.setPassword("mySecret@");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Puis connexion
        String loginJson = """
        {
          "email":"login@example.com",
          "password":"mySecret@"
        }
        """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("login@example.com"))
                .andExpect(jsonPath("$.role").value("PENDING"));
    }

    @Test
    void testLoginBadPassword_ShouldReturnUnauthorized() throws Exception {
        // Compte existant
        AppUserRegisterDto dto = new AppUserRegisterDto();
        dto.setEmail("wrongpass@example.com");
        dto.setFirstName("Wrong");
        dto.setLastName("Pass");
        dto.setPassword("correct123@");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Tentative avec mauvais mot de passe
        String loginJson = """
        {
          "email":"wrongpass@example.com",
          "password":"incorrect"
        }
        """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginDisabledAccount_ShouldReturnForbidden() throws Exception {
        // Création directe du user désactivé en base
        AppUser u = new AppUser();
        u.setEmail("disabled@example.com");
        u.setFirstName("Disabled");
        u.setLastName("User");
        u.setPassword(passwordEncoder.encode("pw"));  // injecter PasswordEncoder en field @Autowired
        u.setRole(PENDING);
        u.setActive(false);
        appUserRepository.save(u);

        // Tentative login
        String loginJson = """
        {
          "email":"disabled@example.com",
          "password":"pw"
        }
        """;
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPaginated_ShouldReturnOk() throws Exception {
        AppUser user1 = new AppUser();
        user1.setEmail("john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPassword("password123@");
        user1.setRole(USER);
        user1.setActive(true);
        appUserRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setPassword("password123@");
        user2.setRole(USER);
        user2.setActive(true);
        appUserRepository.save(user2);

        mockMvc.perform(get("/api/users/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.content[1].email").value("jane@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchByLastName_ShouldReturnMatchingUsers() throws Exception {
        AppUser user1 = new AppUser();
        user1.setEmail("match@example.com");
        user1.setFirstName("Lucie");
        user1.setLastName("Durand");
        user1.setPassword("password123@");
        user1.setRole(USER);
        user1.setActive(true);
        appUserRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setEmail("nonmatch@example.com");
        user2.setFirstName("Jean");
        user2.setLastName("Martin");
        user2.setPassword("password123@");
        user2.setRole(USER);
        user2.setActive(true);
        appUserRepository.save(user2);

        mockMvc.perform(get("/api/users/search?name=Durand&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].lastName").value("Durand"));
    }
}
