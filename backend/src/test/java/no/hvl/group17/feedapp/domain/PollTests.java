package no.hvl.group17.feedapp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PollTests {
    @Test
    void pollOk_returnsTrue() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        var poll = Poll.builder()
                .id(1)
                .question("Favorite food?")
                .options(
                        Arrays.asList(Option.builder().id(1).caption("Pancakes").build(),
                                Option.builder().id(3).caption("Dumplings").build())
                )
                .user(user)
                .publishedAt(Instant.now())
                .build();

        assertThat(poll.Verify()).isTrue();
    }

    @Test
    void pollQuestionMissing_returnsFalse() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        var poll = Poll.builder()
                .id(1)
                .options(
                        Arrays.asList(Option.builder().id(1).caption("Pancakes").build(),
                                Option.builder().id(2).caption("Pizza").build(),
                                Option.builder().id(3).caption("Dumplings").build())
                )
                .user(user)
                .publishedAt(Instant.now())
                .build();

        assertThat(poll.Verify()).isFalse();
    }

    @Test
    void pollUserMissing_returnsFalse() {
        var poll = Poll.builder()
                .id(1)
                .question("Favorite food?")
                .options(
                        Arrays.asList(Option.builder().id(1).caption("Pancakes").build(),
                                Option.builder().id(2).caption("Pizza").build(),
                                Option.builder().id(3).caption("Dumplings").build())
                )
                .publishedAt(Instant.now())
                .build();

        assertThat(poll.Verify()).isFalse();
    }

    @Test
    void pollOptionsMissing_returnsFalse() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        var poll = Poll.builder()
                .id(1)
                .question("Favorite food?")
                .user(user)
                .publishedAt(Instant.now())
                .build();

        assertThat(poll.Verify()).isFalse();
    }

    @Test
    void pollTooLesOptions_returnsFalse() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        var poll = Poll.builder()
                .id(1)
                .question("Favorite food?")
                .options(
                        Collections.singletonList(Option.builder().id(3).caption("Dumplings").build())
                )
                .user(user)
                .publishedAt(Instant.now())
                .build();

        assertThat(poll.Verify()).isFalse();
    }

}
