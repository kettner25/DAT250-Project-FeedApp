package no.hvl.group17.feedapp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserTests {
    @Test
    void userOk_returnsTrue() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .email("alice@mail.com")
                .build();

        assertThat(user.Verify()).isTrue();
    }

    @Test
    void userMissingKID_returnsFalse() {
        var user = User.builder()
                .id(1)
                .username("alice")
                .email("alice@mail.com")
                .build();

        assertThat(user.Verify()).isFalse();
    }

    @Test
    void userMissingUsername_returnsFalse() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .email("alice@mail.com")
                .build();

        assertThat(user.Verify()).isFalse();
    }

    @Test
    void userMissingEmail_returnsFalse() {
        var user = User.builder()
                .id(1)
                .keycloakId("1")
                .username("alice")
                .build();

        assertThat(user.Verify()).isFalse();
    }
}
