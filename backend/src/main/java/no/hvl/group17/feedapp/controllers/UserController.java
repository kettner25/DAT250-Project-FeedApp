package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /// ADMIN only
    @GetMapping("/")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    /// ADMIN and USER.id = uid
    @GetMapping("/{uid}")
    public User getUser(@PathVariable int uid) {
        return userService.getUser(uid);
    }

    /// Public
    /// For now ... but maybe it will be only for ADMIN depending on keycloak
    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        if (!user.Verify()) return null;

        return userService.createUser(user);
    }


    /// ADMIN and USER.id = uid
    @PutMapping("/{uid}")
    public User editUser(@PathVariable int uid, @RequestBody User user) {
        if (!user.Verify()) return null;

        if (uid != user.getId()) return null;

        return userService.editUser(user);
    }

    /// ADMIN and USER.id = uid
    @DeleteMapping("/{uid}")
    public Boolean deleteUser(@PathVariable int uid) {
        return userService.deleteUserById(uid);
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
