package no.hvl.group17.feedapp.repositories;

import jakarta.transaction.Transactional;
import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class VoteRepoTests {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private VoteRepo voteRepository;

    @BeforeEach
    void setup() {
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

        voteRepository.save(Vote.builder()
                        .option(pollRepository.findAll().getFirst().getOptions().get(1))
                        .user(userRepository.findAll().getFirst())
                        .publishedAt(Instant.now())
                .build());
        voteRepository.save(Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .user(userRepository.findAll().getLast())
                .publishedAt(Instant.now())
                .build());

        voteRepository.save(Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .anonId("10203040506")
                .publishedAt(Instant.now())
                .build());
    }

    @AfterEach
    void tearDown() {
        voteRepository.deleteAll();
        pollRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getListOfVotesCountByPollID_returnsList() {
        var list = voteRepository.getVoteCountByPoll(pollRepository.findAll().getFirst().getId());

        assertThat(list.size()).isEqualTo(3);
        assertThat((int) list.get(1)[0]).isEqualTo(pollRepository.findAll().getFirst().getOptions().get(1).getId());
        assertThat((long) list.get(1)[1]).isEqualTo(3);
    }

    @Test
    void getListOfVotesCountByPollID_returnsEmpty() {
        var list = voteRepository.getVoteCountByPoll(-1);
        assertThat(list).isEmpty();
    }

    @Test
    void existsVoteByAnonID_ReturnTrue() {
        var True = voteRepository.existsVoteByAnonID("10203040506", pollRepository.findAll().getFirst().getOptions().get(1).getId());
        assertThat(True).isTrue();
    }

    @Test
    void existsVoteByAnonID_ReturnFalse() {
        var False = voteRepository.existsVoteByAnonID("_10203040506", pollRepository.findAll().getFirst().getOptions().get(1).getId());
        assertThat(False).isFalse();
    }

    @Test
    void existsVoteByUserID_ReturnTrue() {
        var True = voteRepository.existsVoteByUserID(userRepository.findAll().getFirst().getId(), pollRepository.findAll().getFirst().getOptions().get(1).getId());
        assertThat(True).isTrue();
    }

    @Test
    void existsVoteByUserID_ReturnFalse() {
        var False = voteRepository.existsVoteByUserID(-1, pollRepository.findAll().getFirst().getOptions().get(1).getId());
        assertThat(False).isFalse();

        False = voteRepository.existsVoteByUserID(userRepository.findAll().getFirst().getId(), pollRepository.findAll().getFirst().getOptions().get(0).getId());
        assertThat(False).isFalse();
    }
}
