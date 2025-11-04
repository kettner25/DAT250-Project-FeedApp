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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PollRepoTests {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PollRepo pollRepository;

    @BeforeEach
    void setup() {
        userRepository.save(User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        pollRepository.deleteAll();
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
