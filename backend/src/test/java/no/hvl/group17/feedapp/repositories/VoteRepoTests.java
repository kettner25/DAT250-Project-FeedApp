package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build());
        userRepository.save(User.builder()
                .id(2)
                .keycloakId("2")
                .username("bob")
                .email("bob@mail.com")
                .build());

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
                .build());

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
        userRepository.deleteAll();
        pollRepository.deleteAll();
        voteRepository.deleteAll();
    }

    @Test
    void getListOfVotesCountByPollID_returnsList() {
        var list = voteRepository.getVoteCountByPoll(1);

        assertThat(list.size()).isEqualTo(3);
        assertThat((int) list.get(1)[0]).isEqualTo(2);
        assertThat((int) list.get(1)[1]).isEqualTo(3);
    }

    @Test
    void getListOfVotesCountByPollID_returnsEmpty() {
        var list = voteRepository.getVoteCountByPoll(2);
        assertThat(list).isEmpty();
    }

    @Test
    void existsVoteByAnonID_ReturnTrue() {
        var True = voteRepository.existsVoteByAnonID("10203040506", 2);
        assertThat(True).isTrue();
    }

    @Test
    void existsVoteByAnonID_ReturnFalse() {
        var False = voteRepository.existsVoteByAnonID("_10203040506", 2);
        assertThat(False).isFalse();
    }

    @Test
    void existsVoteByUserID_ReturnTrue() {
        var True = voteRepository.existsVoteByUserID(1, 2);
        assertThat(True).isTrue();
    }

    @Test
    void existsVoteByUserID_ReturnFalse() {
        var False = voteRepository.existsVoteByUserID(1, 2);
        assertThat(False).isFalse();
    }
}
