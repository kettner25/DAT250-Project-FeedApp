package no.hvl.group17.feedapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import no.hvl.group17.feedapp.TestMockingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PollControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    RabbitTemplate template;

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
                .claim("sub", "1")
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
                .build()
        );

        pollRepository.deleteAll();

        var poll = Poll.builder()
                .question("Favorite food?")
                .options(
                        Arrays.asList(Option.builder().caption("Pancakes").build(),
                                Option.builder().caption("Pizza").build(),
                                Option.builder().caption("Dumplings").build())
                )
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build();
        poll.linkOptions();
        pollRepository.save(poll);

        poll = Poll.builder()
                .question("Delete?")
                .options(
                        Arrays.asList(Option.builder().caption("Jep").build(),
                                Option.builder().caption("Nope").build())
                )
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build();
        poll.linkOptions();
        pollRepository.save(poll);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPolls_publicAllowed_asAdmin() throws Exception {
        Jwt jwt = createJwtAdmin();

        mockMvc.perform(get("/api/polls").with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Favorite food?"));
    }

    @Test
    @WithMockUser(roles = "User")
    void getAllPolls_publicAllowed_asUser() throws Exception {
        Jwt  jwt = createJwtUser();

        mockMvc.perform(get("/api/polls").with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Favorite food?"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPoll_asAdmin_ok() throws Exception {
        Jwt jwt = createJwtAdmin();
        var user = userRepository.findAll().get(0);

        mockMvc.perform(post("/api/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite book?\", \"user\": "+objectMapper.writeValueAsString(user)+", " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}")
                        .with(jwt().jwt(jwt).authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("Favorite book?"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPoll_asUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();

        mockMvc.perform(post("/api/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite book?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}")
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(post("/api/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite book?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editPoll_asAdmin_ok() throws Exception {
        Jwt jwt = createJwtAdmin();
        var user = userRepository.findAll().get(0);
        var poll = pollRepository.findAll().get(0);

        mockMvc.perform(put("/api/polls/"+poll.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": " + poll.getId() +
                                ",\"question\": \"Favorite food?\", \"user\": "+objectMapper.writeValueAsString(user)+", " +
                                "\"options\": [{\"caption\":  \"Pancakes\"}, {\"caption\": \"Pizza\"}, {\"caption\": \"Cesky Knedlik\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}")
                        .with(jwt().jwt(jwt).authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Favorite food?"))
                .andExpect(jsonPath("$.options", hasSize(4)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void editPoll_asUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();

        mockMvc.perform(put("/api/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}")
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }

    @Test
    void editPoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(put("/api/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePoll_asAdmin_ok() throws Exception {
        Jwt jwt = createJwtAdmin();
        var poll = pollRepository.findAll().getFirst();

        mockMvc.perform(delete("/api/polls/"+poll.getId())
                        .with(jwt().jwt(jwt).authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").value("true"));
    }
    @Test
    @WithMockUser(roles = "USER")
    void deletePoll_asOtherUser_forbidden() throws Exception {
        Jwt jwt = createJwtUser();

        mockMvc.perform(delete("/api/polls/1")
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isForbidden());
    }
    @Test
    void deletePoll_asAnonymous_forbidden() throws Exception {
        mockMvc.perform(delete("/api/polls/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPollCount_publicAllowed() throws Exception {
        mockMvc.perform(get("/api/polls/1/count"))
                .andExpect(status().isNoContent())
                .andExpect(content().string("[]"));
    }
}
