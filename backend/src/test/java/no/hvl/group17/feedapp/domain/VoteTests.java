package no.hvl.group17.feedapp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VoteTests {
    @Test
    void voteRegUserOK_returnsTrue() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        var option = Option.builder().id(1).caption("Pancakes").build();

        var vote = Vote.builder()
                .id(1)
                .option(option)
                .user(user)
                .publishedAt(Instant.now())
                .build();

        assertThat(vote.Verify()).isTrue();
    }

    @Test
    void voteAnonUserOK_returnsTrue() {
        var option = Option.builder().id(1).caption("Pancakes").build();

        var vote = Vote.builder()
                .id(1)
                .option(option)
                .anonId("kjkgddajkh")
                .publishedAt(Instant.now())
                .build();

        assertThat(vote.Verify()).isTrue();
    }

    @Test
    void voteUserMissing_returnsFalse() {
        var option = Option.builder().id(1).caption("Pancakes").build();

        var vote = Vote.builder()
                .id(1)
                .option(option)
                .publishedAt(Instant.now())
                .build();

        assertThat(vote.Verify()).isFalse();
    }

    @Test
    void voteOptionMissing_returnsFalse() {
        var option = Option.builder().id(1).caption("Pancakes").build();

        var vote = Vote.builder()
                .id(1)
                .anonId("kjkgddajkh")
                .publishedAt(Instant.now())
                .build();

        assertThat(vote.Verify()).isFalse();
    }
}
