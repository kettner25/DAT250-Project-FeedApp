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

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
        pollRepository.save(Poll.builder()
                .id(3)
                .question("Delete?")
                .options(
                        Arrays.asList(Option.builder().id(6).caption("Jep").build(),
                                Option.builder().id(7).caption("Nope").build())
                )
                .user(userRepository.findAll().getLast())
                .publishedAt(Instant.now())
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        pollRepository.deleteAll();
    }

    @Test
    void getAllPolls_returnsPolls() {
        var polls = pollService.getAllPolls();

        assertThat(polls).hasSize(3);
        assertThat(polls).extracting(Poll::getId).isEqualTo(Arrays.asList(1, 2, 3));
        assertThat(polls).extracting(Poll::getQuestion).isEqualTo(Arrays.asList("Favorite food?", "Delete?", "Delete?"));
        assertThat(polls).extracting(Poll::getUser).extracting(User::getId).isEqualTo(Arrays.asList(1, 1, 2));
    }

    @Test
    void getPollById_returnsPoll() {
        var poll = pollService.getPollById(1);

        assertThat(poll.getId()).isEqualTo(1);
        assertThat(poll.getQuestion()).isEqualTo("Favorite food?");
    }

    @Test
    void getPollById_returnsNull() {
        var poll = pollService.getPollById(10);
        assertThat(poll).isNull();
    }

    @Test
    void getAllPollsByUser_returnsPolls() {
        var polls = pollService.getAllPollsByUser(1);

        assertThat(polls).hasSize(2);
        assertThat(polls).extracting(Poll::getId).isEqualTo(Arrays.asList(1, 2));
        assertThat(polls).extracting(Poll::getQuestion).isEqualTo(Arrays.asList("Favorite food?", "Delete?"));
    }

    @Test
    void getAllPollsByUser_returnsEmpty() {
        var polls = pollService.getAllPollsByUser(3);
        assertThat(polls).isEmpty();
    }

    @Test
    void getPollByUser_returnsPoll() {
        var poll = pollService.getPollByIdByUser(2, 3);

        assertThat(poll.getId()).isEqualTo(3);
        assertThat(poll.getQuestion()).isEqualTo("Delete?");
    }

    @Test
    void createPoll_returnsPoll() {
        var poll = pollService.createPoll(Poll.builder()
                        .id(4)
                        .user(userRepository.findAll().getFirst())
                        .publishedAt(Instant.now())
                        .question("Favorite food 2?")
                        .options(Arrays.asList(Option.builder().id(6).caption("Jep").build(),
                                Option.builder().id(7).caption("Nope").build()))
                .build());

        assertThat(poll.getId()).isEqualTo(4);
        assertThat(poll.getQuestion()).isEqualTo("Favorite food 2?");
        assertThat(poll.getOptions().size()).isEqualTo(2);

        assertThat(pollService.getAllPolls().size()).isEqualTo(4);
    }

    @Test
    void editPoll_returnsPoll() {
        var poll = pollService.getPollById(1);

        var date = Instant.now();
        poll.setValidUntil(date);

        var poll1 = pollService.editPoll(poll);
        assertThat(poll1.getId()).isEqualTo(1);
        assertThat(poll1.getValidUntil()).isEqualTo(date);

        assertThat(pollService.getAllPolls().size()).isEqualTo(3);
    }

    @Test
    void editPoll_returnsNull() {
        var poll = pollService.getPollById(4);

        var date = Instant.now();
        poll.setValidUntil(date);

        var poll1 = pollService.editPoll(poll);
        assertThat(poll1).isNull();

        assertThat(pollService.getAllPolls().size()).isEqualTo(3);
    }

    @Test
    void deletePollById_returnsTrue() {
        var True = pollService.deletePollById(2);
        assertThat(True).isTrue();
    }

    @Test
    void deletePollById_returnsFalse() {
        var False = pollService.deletePollById(7);
        assertThat(False).isFalse();
    }
}
