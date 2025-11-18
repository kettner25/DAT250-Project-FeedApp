package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTests {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAllInBatch();

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
        userRepository.save(User.builder()
                .keycloakId("3")
                .username("delete")
                .email("delete@mail.com")
                .build());
        userRepository.save(User.builder()
                .keycloakId("4")
                .username("delete1")
                .email("delete1@mail.com")
                .build());
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        var users = userService.getAllUsers();

        // Assert
        assertThat(users).hasSize(4);
        assertThat(users).extracting(User::getUsername).isEqualTo(Arrays.asList("alice", "bob", "delete", "delete1"));
    }

    @Test
    void getUserById_returnsUser() {
        var user1 = userService.getUser(userRepository.findAll().getFirst().getId());
        assertThat(user1.getId()).isEqualTo(userRepository.findAll().get(0).getId());
        assertThat(user1.getUsername()).isEqualTo("alice");

        var user2 = userService.getUser(userRepository.findAll().get(1).getId());
        assertThat(user2.getId()).isEqualTo(userRepository.findAll().get(1).getId());
        assertThat(user2.getUsername()).isEqualTo("bob");
    }

    @Test
    void getUserById_returnsNull() {
        var user1 = userService.getUser(-1);
        assertThat(user1).isNull();
    }

    @Test
    void getUserByUsername_returnsUser() {
        var user1 = userService.getUser("alice");
        assertThat(user1.getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(user1.getUsername()).isEqualTo("alice");

        var user2 = userService.getUser("delete");
        assertThat(user2.getId()).isEqualTo(userRepository.findAll().get(2).getId());
        assertThat(user2.getUsername()).isEqualTo("delete");
    }

    @Test
    void getUserByUsername_returnsNull() {
        var user1 = userService.getUser("a");
        assertThat(user1).isNull();
    }

    @Test
    void getUserByEmail_returnsUser() {
        var user1 = userService.getUserByEmail("alice@mail.com");
        assertThat(user1.getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(user1.getUsername()).isEqualTo("alice");
        assertThat(user1.getEmail()).isEqualTo("alice@mail.com");
    }

    @Test
    void getUserByEmail_returnsNull() {
        var user1 = userService.getUserByEmail("a");
        assertThat(user1).isNull();
    }

    @Test
    void isUnique_returnsTrue() {
        var user = User.builder()
                .keycloakId("5")
                .username("alice-kernel")
                .email("alice.kernel@emal.com")
                .build();

        var True = userService.isUnique(user);
        assertThat(True).isTrue();
    }

    @Test
    void isUnique_returnsFalse() {
        var user = User.builder()
                .keycloakId("5")
                .username("alice")
                .email("alice.kernel@emal.com")
                .build();

        var False = userService.isUnique(user);
        assertThat(False).isFalse();

        user.setUsername("alice1");
        user.setEmail("alice@mail.com");

        False = userService.isUnique(user);
        assertThat(False).isFalse();
    }

    @Test
    void createUser_returnsUser() {
        var user = User.builder()
                .keycloakId("5")
                .username("alice-kernel")
                .email("alice.kernel@emal.com")
                .build();

        var userCreated = userService.createUser(user);
        assertThat(userCreated.getId()).isEqualTo(userRepository.findAll().getLast().getId());
        assertThat(userCreated.getUsername()).isEqualTo("alice-kernel");
        assertThat(userCreated).isEqualTo(user);

        //Check if added really
        var users = userService.getAllUsers();
        assertThat(users).hasSize(5);
    }

    @Test
    void createUser_returnNull() {
        var user = User.builder()
                .keycloakId("5")
                .username("alice")
                .email("alice.kernel@emal.com")
                .build();

        var userCreated = userService.createUser(user);
        assertThat(userCreated).isNull();

        //Check if added really
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void updateUser_returnsUser() {
        var user = userService.getUser(userRepository.findAll().getFirst().getId());

        user.setEmail("Alice.ana@gmail.com");
        var edited = userService.editUser(user.getId(), user);

        assertThat(edited.getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(edited.getEmail()).isEqualTo(user.getEmail());

        //Check that not added
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void updateUser_returnsNull() {
        var user = userService.getUser(userRepository.findAll().getFirst().getId());

        user.setEmail("delete@mail.com");
        var edited = userService.editUser(user.getId(), user);

        assertThat(edited).isNull();

        //Check that not added
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void updateUser_noUpdates_returnsUser() {
        var user = userService.getUser(userRepository.findAll().getFirst().getId());

        var edited = userService.editUser(user.getId(), user);

        assertThat(edited.getId()).isEqualTo(userRepository.findAll().getFirst().getId());
        assertThat(edited.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void updateNewUser_returnsNull() {
        var user = User.builder()
                .keycloakId("5")
                .username("alice-kernel")
                .email("alice.kernel@emal.com")
                .build();

        var edited = userService.editUser(0, user);

        assertThat(edited).isEqualTo(null);

        //Check that not added
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void deleteUserById_returnsTrue() {
        var True = userService.deleteUserById(userRepository.findAll().getLast().getId());

        assertThat(True).isTrue();

        //Check that deleted
        var users = userService.getAllUsers();
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getUsername).isEqualTo(Arrays.asList("alice", "bob", "delete"));

        //--- Delete another one

        True = userService.deleteUserById(userRepository.findAll().getLast().getId());
        assertThat(True).isTrue();

        users = userService.getAllUsers();
        assertThat(users).hasSize(2);
    }
}
