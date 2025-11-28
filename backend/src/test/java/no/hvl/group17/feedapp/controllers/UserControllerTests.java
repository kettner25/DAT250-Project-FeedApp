package no.hvl.group17.feedapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Jwt createJwtUser() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "1")
                .claim("preferred_username", "john.doe")
                .claim("email", "john.doe@mail.com")
                .claim("roles", List.of("USER"))
                .build();
    }

    private Jwt createJwtAdmin() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "admin")
                .claim("preferred_username", "john1.doe")
                .claim("email", "john1.doe@mail.com")
                .claim("roles", List.of("ADMIN"))
                .build();
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build());
        userRepository.save(User.builder()
                .keycloakId("2")
                .username("bob")
                .email("bob@mail.com")
                .build());
        userRepository.save(User.builder()
                .keycloakId("3")
                .username("delete")
                .email("delete@mail.com")
                .build());
        userRepository.save(User.builder()
                .keycloakId("4")
                .username("delete1")
                .email("delete1@mail.com")
                .build());
        userRepository.save(User.builder()
                .keycloakId("admin")
                .username("admin")
                .email("admin@aaa.c")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_asAdmin_returnsAll() throws Exception {
        Jwt jwt = createJwtAdmin();

        mockMvc.perform(get("/api/users")
                        .with(jwt().jwt(jwt))
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].username", is("alice")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_asUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();

        mockMvc.perform(get("/api/users").with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUser_asOtherUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();
        var user = userRepository.findAll().get(0);

        mockMvc.perform(get("/api/users/"+user.getId()+1).with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_asAnonymous_forbidden() throws Exception {
        Jwt jwt = createJwtUser();
        var user = userRepository.findAll().get(0);

        mockMvc.perform(get("/api/users/"+user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void editUser_asOtherUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();
        var user = userRepository.findAll().get(1);

        mockMvc.perform(put("/api/users/"+user.getId()).with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void editUser_asAnonymous_forbidden() throws Exception {
        var user = userRepository.findAll().get(1);

        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUser_asAdmin_returnsNewData() throws Exception {
        Jwt jwt = createJwtAdmin();
        var user = userRepository.findAll().get(1);
        user.setEmail("bob@centrum.cz");

        mockMvc.perform(put("/api/users/"+user.getId())
                            .with(jwt().jwt(jwt))
                            .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("bob")))
                .andExpect(jsonPath("$.email", is("bob@centrum.cz")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_asOtherUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();
        var user = userRepository.findAll().get(2);

        mockMvc.perform(delete("/api/users/"+user.getId()).with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_asAdmin_true() throws Exception {
        Jwt  jwt = createJwtAdmin();
        var user = userRepository.findAll().get(1);

        mockMvc.perform(delete("/api/users/"+user.getId())
                        .with(jwt().jwt(jwt))
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").value("true"));
    }
}