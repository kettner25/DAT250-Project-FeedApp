package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Temporarily disabled until AUTH is ready")
// todo fix tests
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepository;

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
                .email("delete@mail.com")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_asAdmin_returnsAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("alice")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_asUser_forbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void getUser_asSelf_returnsOwnData() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("alice")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void getUser_asOtherUser_forbidden() throws Exception {
        mockMvc.perform(get("/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(get("/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_withoutAuth_allowed() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"charlie\", \"email\": \"charlie@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("charlie")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void editUser_asSelf_returnsOwnData() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"alice@centrum.cz\"}"))
                .andExpect(jsonPath("$.username", is("alice")))
                .andExpect(jsonPath("$.email", is("alice@centrum.cz")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void editUser_asOtherUser_forbidden() throws Exception {
        mockMvc.perform(put("/users/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\": \"alice@centrum.cz\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void editUser_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"alice@centrum.cz\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUser_asAdmin_returnsNewData() throws Exception {
        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"bob@centrum.cz\"}"))
                .andExpect(jsonPath("$.username", is("bob")))
                .andExpect(jsonPath("$.email", is("bob@centrum.cz")));
    }

    @Test
    @WithMockUser(username = "3", roles = "USER")
    void deleteUser_asSelf_true() throws Exception {
        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("true")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deleteUser_asOtherUser_forbidden() throws Exception {
        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_asAdmin_true() throws Exception {
        mockMvc.perform(delete("/users/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("true")));
    }
}