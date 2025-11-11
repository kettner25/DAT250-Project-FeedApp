package no.hvl.group17.feedapp.repositories;

import jakarta.transaction.Transactional;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepoTests {

    @Autowired
    private UserRepo userRepository;

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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_returnUser() {
        var user = userRepository.findByUsername("alice");

        assertThat(user.orElseThrow().getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(user.orElseThrow().getUsername()).isEqualTo("alice");

        user = userRepository.findByUsername("bob");
        assertThat(user.orElseThrow().getId()).isEqualTo(userRepository.findAll().get(1).getId());
        assertThat(user.orElseThrow().getUsername()).isEqualTo("bob");
    }

    @Test
    void findByUsername_returnNull() {
        var user = userRepository.findByUsername("Ahoj");
        assertThat(user).isEqualTo(Optional.empty());
    }

    @Test
    void findByEmail_returnUser() {
        var user = userRepository.findByEmail("alice@mail.com");

        assertThat(user.orElseThrow().getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(user.orElseThrow().getUsername()).isEqualTo("alice");
        assertThat(user.orElseThrow().getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void findByEmail_returnNull() {
        var user = userRepository.findByEmail("a@mail.com");
        assertThat(user).isEmpty();
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
