package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import no.hvl.group17.feedapp.repositories.VoteRepo;
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
public class PollVotesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private VoteRepo voteRepository;

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

        voteRepository.deleteAll();
        voteRepository.save(Vote.builder().id(1).user(userRepository.findAll().getFirst()).option(pollRepository.findAll().getFirst().getOptions().getFirst()).build());
    }

    @Test
    void vote_withoutAuth_allowed() throws Exception {
        mockMvc.perform(post("/polls/1/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\":{\"id\": 1}, \"option\":{\"id\": 2}}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void unvote_asOwner_allowed() throws Exception {
        mockMvc.perform(delete("/polls/1/votes/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "2", roles = "USER")
    void unvote_asOtherUser_forbidden() throws Exception {
        mockMvc.perform(delete("/polls/1/votes/1"))
                .andExpect(status().isForbidden());
    }
}
