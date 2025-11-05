package no.hvl.group17.feedapp.services;

import jakarta.transaction.Transactional;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
    void getVoteById_returnsVote() {
        Vote vote = voteService.getVoteById(voteRepository.findAll().getFirst().getId());

        assertThat(vote.getId()).isEqualTo(voteRepository.findAll().getFirst().getId());
        assertThat(vote.getUser().getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(vote.getOption().getId()).isEqualTo(pollRepository.findAll().getFirst().getOptions().get(1).getId());
    }

    @Test
    void getVoteById_returnsNull() {
        Vote vote = voteService.getVoteById(-1);

        assertThat(vote).isNull();
    }

    @Test
    void getVoteCountByPollID_returnsVoteCounts() {
        var list = voteService.getVoteCountsByPoll(pollRepository.findAll().getFirst().getId());

        assertThat(list).hasSize(3);
        assertThat(list).extracting(OptionCount::getOption).extracting(Option::getCaption).containsExactly("Pancakes", "Pizza", "Dumplings");
        assertThat(list).extracting(OptionCount::getCount).containsExactly(0, 2, 0);
    }

    @Test
    void getVoteCountByPollID_returnsNull() {
        var list = voteService.getVoteCountsByPoll(-1);
        assertThat(list).isEmpty();
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
        var a = voteRepository.findAll();
        var True = voteService.deleteVote(voteRepository.findAll().getFirst().getId());

        voteRepository.flush();
        assertThat(True).isTrue();
        a = voteRepository.findAll();
        assertThat(voteRepository.findAll().size()).isEqualTo(1);
        assertThat(voteRepository.existsVoteByUserID(userRepository.findAll().getFirst().getId(),
                pollRepository.findAll().getFirst().getOptions().get(1).getId())).isEqualTo(false);
    }

    @Test
    void deleteVoteById_returnsFalse() {
        var False = voteService.deleteVote(-1);

        assertThat(False).isFalse();
        assertThat(voteRepository.findAll().size()).isEqualTo(2);
    }
}
