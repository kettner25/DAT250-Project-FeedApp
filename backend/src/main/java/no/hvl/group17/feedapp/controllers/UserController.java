package no.hvl.group17.feedapp.controllers;

import jdk.jshell.spi.ExecutionControl;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public List<User> getAll() {
        throw new RuntimeException("Not Implemented");
    }

    @GetMapping("/{uid}")
    public User getUser(@PathVariable int uid) {
        throw new RuntimeException("Not Implemented");
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        throw new RuntimeException("Not Implemented");
    }

    @PutMapping("/{uid}")
    public User editUser(@PathVariable int uid, @RequestBody User user) {
        throw new RuntimeException("Not Implemented");
    }

    @DeleteMapping("/{uid}")
    public Boolean deleteUser() {
        throw new RuntimeException("Not Implemented");
    }

    //---------------------------------------------------
    //---------------Depending on Keycloak---------------
    //---------------------------------------------------

    @GetMapping("/login")
    public String login() {
        throw new RuntimeException("Not Implemented");
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        throw new RuntimeException("Not Implemented");
    }

    @GetMapping("/logout")
    public String logout() {
        throw new RuntimeException("Not Implemented");
    }
}
