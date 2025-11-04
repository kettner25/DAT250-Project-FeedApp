package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
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
        userRepository.save(User.builder()
                .id(3)
                .keycloakId("3")
                .username("delete")
                .email("delete@mail.com")
                .build());
        userRepository.save(User.builder()
                .id(4)
                .keycloakId("4")
                .username("delete1")
                .email("delete@mail.com")
                .build());
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        var users = userService.getAllUsers();

        // Assert
        assertThat(users).hasSize(4);
        assertThat(users).extracting(User::getId).isEqualTo(Arrays.asList(1, 2, 3, 4));
        assertThat(users).extracting(User::getUsername).isEqualTo(Arrays.asList("alice", "bob", "delete", "delete1"));

        //This line checks that findAll() was called
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_returnsUser() {
        var user1 = userService.getUserById(1);
        assertThat(user1.getId()).isEqualTo(1);
        assertThat(user1.getUsername()).isEqualTo("alice");

        var user2 = userService.getUserById(2);
        assertThat(user2.getId()).isEqualTo(2);
        assertThat(user2.getUsername()).isEqualTo("bob");

        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
    }

    @Test
    void createUser_returnsUser() {
        var user = User.builder()
                .id(5)
                .keycloakId("5")
                .username("alice-kernel")
                .email("alice.kernel@emal.com")
                .build();

        var userCreated = userService.createUser(user);
        assertThat(userCreated.getId()).isEqualTo(5);
        assertThat(userCreated.getUsername()).isEqualTo("alice-kernel");
        assertThat(userCreated).isEqualTo(user);

        verify(userRepository).save(userCreated);

        //Check if added really
        var users = userService.getAllUsers();
        assertThat(users).hasSize(5);
    }

    @Test
    void updateUser_returnsUser() {
        var user = userService.getUserById(1);

        user.setEmail("Alice.ana@gmail.com");
        var edited = userService.editUser(user);

        assertThat(edited.getId()).isEqualTo(1);
        assertThat(edited.getEmail()).isEqualTo(user.getEmail());

        //Check that not addded
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void updateNewUser_returnsNull() {
        var user = User.builder()
                .id(5)
                .keycloakId("5")
                .username("alice-kernel")
                .email("alice.kernel@emal.com")
                .build();

        var edited = userService.editUser(user);

        assertThat(edited).isEqualTo(null);

        //Check that not addded
        var users = userService.getAllUsers();
        assertThat(users).hasSize(4);
    }

    @Test
    void deleteUserById_returnsTrue() {
        var True = userService.deleteUserById(4);

        assertThat(True).isTrue();

        verify(userRepository).deleteById(4);

        //Check that deleted
        var users = userService.getAllUsers();
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getId).isEqualTo(Arrays.asList(1, 2, 3));

        //--- Delete another one

        True = userService.deleteUserById(3);
        assertThat(True).isTrue();
        verify(userRepository).deleteById(3);

        users = userService.getAllUsers();
        assertThat(users).hasSize(2);
    }
}
