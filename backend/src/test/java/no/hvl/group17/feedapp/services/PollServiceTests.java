package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
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
public class PollServiceTests {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PollRepo pollRepository;

    @Autowired
    private PollService pollService;

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

    @AfterEach
    void tearDown() {
        pollRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllPolls_returnsPolls() {
        var polls = pollService.getAllPolls();

        assertThat(polls).hasSize(3);
        assertThat(polls).extracting(Poll::getQuestion).isEqualTo(Arrays.asList("Favorite food?", "Delete?", "Delete?"));
        assertThat(polls).extracting(Poll::getUser).extracting(User::getUsername).isEqualTo(Arrays.asList("alice", "alice", "bob"));
    }

    @Test
    void getPollById_returnsPoll() {
        var poll = pollService.getPollById(pollRepository.findAll().getFirst().getId());

        assertThat(poll.getId()).isEqualTo(pollRepository.findAll().getFirst().getId());
        assertThat(poll.getQuestion()).isEqualTo("Favorite food?");
    }

    @Test
    void getPollById_returnsNull() {
        var poll = pollService.getPollById(-1);
        assertThat(poll).isNull();
    }

    @Test
    void getOptionById_returnsOption() {
        var option = pollService.getOptionById(pollRepository.findAll().getFirst().getOptions().get(0).getId());

        assertThat(option.getId()).isEqualTo(pollRepository.findAll().getFirst().getOptions().get(0).getId());
        assertThat(option.getCaption()).isEqualTo("Pancakes");
    }

    @Test
    void getOptionById_returnsNull() {
        var option = pollService.getOptionById(-1);

        assertThat(option).isNull();
    }

    @Test
    void getAllPollsByUser_returnsPolls() {
        var polls = pollService.getAllPollsByUser(userRepository.findAll().getFirst().getId());

        assertThat(polls).hasSize(2);
        assertThat(polls).extracting(Poll::getQuestion).isEqualTo(Arrays.asList("Favorite food?", "Delete?"));
    }

    @Test
    void getAllPollsByUser_returnsEmpty() {
        var polls = pollService.getAllPollsByUser(-1);
        assertThat(polls).isEmpty();
    }

    @Test
    void getPollByUser_returnsPoll() {
        var poll = pollService.getPollByIdByUser(userRepository.findAll().getLast().getId(), pollRepository.findAll().getLast().getId());

        assertThat(poll.getId()).isEqualTo(pollRepository.findAll().getLast().getId());
        assertThat(poll.getQuestion()).isEqualTo("Delete?");
    }

    @Test
    void createPoll_returnsPoll() {
        var poll = Poll.builder()
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .question("Favorite food 2?")
                .options(Arrays.asList(Option.builder().caption("Jep").build(),
                        Option.builder().caption("Nope").build()))
                .build();
        poll.linkOptions();
        var poll1 = pollService.createPoll(poll);

        assertThat(poll1.getId()).isEqualTo(pollRepository.findAll().getLast().getId());
        assertThat(poll1.getQuestion()).isEqualTo("Favorite food 2?");
        assertThat(poll1.getOptions().size()).isEqualTo(2);

        assertThat(pollService.getAllPolls().size()).isEqualTo(4);
    }

    @Test
    void editPoll_returnsPoll() {
        var poll = pollService.getPollById(pollRepository.findAll().getFirst().getId());

        var date = Instant.now();
        poll.setValidUntil(date);

        var poll1 = pollService.editPoll(poll);
        assertThat(poll1.getId()).isEqualTo(pollRepository.findAll().getFirst().getId());
        assertThat(poll1.getValidUntil()).isEqualTo(date);

        assertThat(pollService.getAllPolls().size()).isEqualTo(3);
    }

    @Test
    void editPoll_returnsNull() {
        var poll = Poll.builder()
                .user(userRepository.findAll().getFirst())
                .publishedAt(Instant.now())
                .question("Favorite food 2?")
                .options(Arrays.asList(Option.builder().caption("Jep").build(),
                        Option.builder().caption("Nope").build()))
                .build();

        var poll1 = pollService.editPoll(poll);
        assertThat(poll1).isNull();

        assertThat(pollService.getAllPolls().size()).isEqualTo(3);
    }

    @Test
    void deletePollById_returnsTrue() {
        var True = pollService.deletePollById(pollRepository.findAll().get(1).getId());
        assertThat(True).isTrue();
    }

    @Test
    void deletePollById_returnsFalse() {
        var False = pollService.deletePollById(-1);
        assertThat(False).isFalse();
    }
}
