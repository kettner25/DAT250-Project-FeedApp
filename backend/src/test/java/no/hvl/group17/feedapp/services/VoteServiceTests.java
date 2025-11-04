package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import no.hvl.group17.feedapp.repositories.VoteRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VoteServiceTests {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private VoteRepo voteRepository;

    @Autowired
    private VoteService voteService;

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
                .id(1)
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build());

        voteRepository.save(Vote.builder()
                .id(2)
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
    void getVoteById_returnsVote() {
        Vote vote = voteService.getVoteById(1);

        assertThat(vote.getId()).isEqualTo(1);
        assertThat(vote.getUser().getId()).isEqualTo(1);
        assertThat(vote.getOption().getId()).isEqualTo(2);
    }

    @Test
    void getVoteById_returnsNull() {
        Vote vote = voteService.getVoteById(5);

        assertThat(vote).isNull();
    }

    @Test
    void getVoteCountByPollID_returnsVoteCounts() {
        var list = voteService.getVoteCountsByPoll(1);

        assertThat(list).hasSize(3);
        assertThat(list).extracting(OptionCount::getOption).extracting(Option::getId).containsExactly(1, 2, 3);
        assertThat(list).extracting(OptionCount::getCount).containsExactly(0, 2, 0);
    }

    @Test
    void getVoteCountByPollID_returnsNull() {
        var list = voteService.getVoteCountsByPoll(5);
        assertThat(list).isNull();
    }

    @Test
    void createVote_returnsTrue() {
        var vote = Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .user(userRepository.findAll().getLast())
                .publishedAt(Instant.now())
                .build();

        var True = voteService.createVote(vote);

        assertThat(True).isTrue();

        vote = Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .anonId("_10203040506_")
                .publishedAt(Instant.now())
                .build();
        True = voteService.createVote(vote);
        assertThat(True).isTrue();
    }

    @Test
    void createVote_returnsFalse() {
        var vote = Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .build();
        var False = voteService.createVote(vote);
        assertThat(False).isFalse();

        vote = Vote.builder()
                .option(pollRepository.findAll().getFirst().getOptions().get(1))
                .anonId("10203040506")
                .publishedAt(Instant.now())
                .build();

        False = voteService.createVote(vote);
        assertThat(False).isFalse();
    }

    @Test
    void deleteVoteById_returnsTrue() {
        var True = voteService.deleteVote(1);

        assertThat(True).isTrue();
        assertThat(voteRepository.findAll().size()).isEqualTo(1);
        assertThat(voteRepository.existsVoteByUserID(1, 2)).isEqualTo(false);
    }

    @Test
    void deleteVoteById_returnsFalse() {
        var False = voteService.deleteVote(5);

        assertThat(False).isFalse();
        assertThat(voteRepository.findAll().size()).isEqualTo(2);
    }
}
