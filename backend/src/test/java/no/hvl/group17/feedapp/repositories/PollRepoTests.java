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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PollRepoTests {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PollRepo pollRepository;

    @BeforeEach
    void setup() {
        userRepository.save(User.builder()
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
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
    }

    @AfterEach
    void tearDown() {
        pollRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getOptionByID_returnsOption() {
        var option = pollRepository.getOptionById(1).orElse(null);

        assertThat(option).isNotNull();
        assertThat(option.getId()).isEqualTo(1);
        assertThat(option.getCaption()).isEqualTo("Pancakes");
    }

    @Test
    void getOptionByID_returnsNull() {
        var option = pollRepository.getOptionById(10).orElse(null);

        assertThat(option).isNull();
    }
}
