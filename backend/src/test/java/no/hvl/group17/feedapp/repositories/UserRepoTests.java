package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepoTests {

    @Autowired
    private UserRepo userRepository;

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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_returnUser() {
        var user = userRepository.findByUsername("alice");

        assertThat(user.orElseThrow().getId()).isEqualTo(1);
        assertThat(user.orElseThrow().getUsername()).isEqualTo("alice");

        user = userRepository.findByUsername("bob");
        assertThat(user.orElseThrow().getId()).isEqualTo(2);
        assertThat(user.orElseThrow().getUsername()).isEqualTo("bob");
    }

    @Test
    void findByUsername_returnNull() {
        var user = userRepository.findByUsername("Ahoj");
        assertThat(user).isEqualTo(null);
    }

    @Test
    void findByEmail_returnUser() {
        var user = userRepository.findByEmail("alice@mail.com");

        assertThat(user.orElseThrow().getId()).isEqualTo(1);
        assertThat(user.orElseThrow().getUsername()).isEqualTo("alice");
        assertThat(user.orElseThrow().getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void findByEmail_returnNull() {
        var user = userRepository.findByEmail("alice@mail.com");
        assertThat(user).isEqualTo(null);
    }

    @Test
    void checkIfUnique_returnUnique() {
        var True = userRepository.checkIfUnique("a", "a@mail.com");
        assertThat(True).isTrue();
    }

    @Test
    void checkIfUnique_UsernameTaken_returnFalse() {
        var False = userRepository.checkIfUnique("alice", "a@mail.com");
        assertThat(False).isFalse();
    }

    @Test
    void checkIfUnique_EmailTaken_returnFalse() {
        var False = userRepository.checkIfUnique("a", "alice@mail.com");
        assertThat(False).isFalse();
    }
}
