package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private UserRepo userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build()
        );

        pollRepository.deleteAll();
        pollRepository.save(Poll.builder()
                .id(1)
                .question("Favorite food?")
                .options(
                        Arrays.asList(Option.builder().id(1).caption("Pancakes").build(),
                                Option.builder().id(2).caption("Pizza").build(),
                                Option.builder().id(3).caption("Dumplings").build())
                )
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build()
        );
        pollRepository.save(Poll.builder()
                .id(2)
                .question("Delete?")
                .options(
                        Arrays.asList(Option.builder().id(4).caption("Jep").build(),
                                Option.builder().id(5).caption("Nope").build())
                )
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPolls_publicAllowed_asAdmin() throws Exception {
        mockMvc.perform(get("/polls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Favorite food?"));
    }

    @Test
    @WithMockUser(roles = "User")
    void getAllPolls_publicAllowed_asUser() throws Exception {
        mockMvc.perform(get("/polls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Favorite food?"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPoll_asAdmin_ok() throws Exception {
        mockMvc.perform(post("/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Favorite book?"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPoll_asUser_forbidden() throws Exception {
        mockMvc.perform(post("/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite book?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(post("/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite book?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editPoll_asAdmin_ok() throws Exception {
        mockMvc.perform(put("/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Favorite food?"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void editPoll_asUser_forbidden() throws Exception {
        mockMvc.perform(put("/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void editPoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(put("/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePoll_asAdmin_ok() throws Exception {
        mockMvc.perform(delete("/polls/2"))
                .andExpect(jsonPath("$").value("true"));
    }
    @Test
    @WithMockUser(roles = "USER")
    void deletePoll_asOtherUser_forbidden() throws Exception {
        mockMvc.perform(delete("/polls/1"))
                .andExpect(status().isForbidden());
    }
    @Test
    void deletePoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(delete("/polls/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPollCount_publicAllowed() throws Exception {
        mockMvc.perform(get("/polls/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
