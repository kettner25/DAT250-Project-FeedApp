package no.hvl.group17.feedapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import no.hvl.group17.feedapp.repositories.VoteRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PollVotesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private VoteRepo voteRepository;

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

    private Jwt createJwtSecUser() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "1adasdsa")
                .claim("preferred_username", "john1.doe")
                .claim("email", "john1.doe@mail.com")
                .claim("roles", List.of("USER"))
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

        voteRepository.deleteAll();
        voteRepository.save(Vote.builder().user(userRepository.findAll().getFirst()).option(pollRepository.findAll().getFirst().getOptions().getFirst()).build());
    }

    @Test
    void vote_withoutAuth_allowed() throws Exception {
        var user = userRepository.findAll().getFirst();
        var option = pollRepository.findAll().get(0).getOptions().get(1);
        var pid = pollRepository.findAll().get(0).getId();

        Jwt jwt = createJwtUser();

        mockMvc.perform(post("/api/polls/"+ pid +"/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\":"+objectMapper.writeValueAsString(user)+", " +
                                "\"option\":"+objectMapper.writeValueAsString(option)+"}," +
                                "\"publishedAt\":"+objectMapper.writeValueAsString(Instant.now()))
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void unvote_asOwner_allowed() throws Exception {
        Jwt jwt = createJwtUser();

        var pid = pollRepository.findAll().get(0).getId();
        var vid = voteRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/polls/"+pid+"/votes/"+vid).with(jwt().jwt(jwt)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "2", roles = "USER")
    void unvote_asOtherUser_forbidden() throws Exception {
        Jwt jwt = createJwtSecUser();

        var pid = pollRepository.findAll().get(0).getId();
        var vid = voteRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/polls/"+pid+"/votes/"+vid).with(jwt().jwt(jwt)))
                .andExpect(status().isBadRequest());
    }
}
