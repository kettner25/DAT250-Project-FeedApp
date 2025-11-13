package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.PollRepo;
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

import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Temporarily disabled until AUTH is ready")
// todo fix
public class UserPollsControllerTests {

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
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build()
        );
        userRepository.save(User.builder()
                .keycloakId("2")
                .username("bob")
                .email("boby@mail.com")
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

        poll = Poll.builder()
                .question("Delete?")
                .options(
                        Arrays.asList(Option.builder().caption("Jep").build(),
                                Option.builder().caption("Nope").build())
                )
                .user(userRepository.findAll().getLast())
                .publishedAt(Instant.now())
                .build();
        poll.linkOptions();
        pollRepository.save(poll);
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void getOwnPolls_returnsPolls() throws Exception {
        mockMvc.perform(get("/users/1/polls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question", is("Favorite food?")));
    }

    @Test
    @WithMockUser(username = "2", roles = "USER")
    void getOtherUserPolls_forbidden() throws Exception {
        mockMvc.perform(get("/users/1/polls"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOtherUserPollsAnonymous_forbidden() throws Exception {
        mockMvc.perform(get("/users/1/polls"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void createPoll_forOwnAccount_allowed() throws Exception {
        mockMvc.perform(post("/users/1/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is("Best movie?")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void createPoll_forAnotherAccount_forbidden() throws Exception {
        mockMvc.perform(post("/users/2/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPoll_forAnotherAccountAnonymous_forbidden() throws Exception {
        mockMvc.perform(post("/users/2/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void editPoll_forOwnAccount_ok() throws Exception {
        mockMvc.perform(put("/users/1/polls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Favorite food?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}, {\"caption\": \"Dumplings\", \"order\": 1}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question", is("Favorite food?")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void editPoll_forAnotherAccount_forbidden() throws Exception {
        mockMvc.perform(put("/users/2/polls/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void editPoll_forAnotherAccount_AsMine_forbidden() throws Exception {
        mockMvc.perform(put("/users/1/polls/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void editPoll_forAnotherAccountAnonymous_forbidden() throws Exception {
        mockMvc.perform(put("/users/2/polls/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\": \"Best movie?\", \"user\": {\"id\": 1}, " +
                                "\"options\": [{\"caption\":  \"a\"}, {\"caption\": \"B\"}]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deletePoll_forOwnAccount_ok() throws Exception {
        mockMvc.perform(delete("/users/1/polls/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("true")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deletePoll_forAnotherAccount_forbidden() throws Exception {
        mockMvc.perform(delete("/users/2/polls/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deletePoll_forAnotherAccount_asMine_forbidden() throws Exception {
        mockMvc.perform(delete("/users/1/polls/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePoll_forAnotherAccountAnonymous_forbidden() throws Exception {
        mockMvc.perform(delete("/users/2/polls/3"))
                .andExpect(status().isForbidden());
    }
}
