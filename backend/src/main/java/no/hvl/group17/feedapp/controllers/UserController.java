package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{uid}")
    public User getUser(@PathVariable int uid) {
        return userService.getUser(uid);
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uid}")
    public User editUser(@PathVariable int uid, @RequestBody User user) {
        if (!user.Verify()) return null;
        return userService.editUser(uid, user);
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uid}")
    public Boolean deleteUser(@PathVariable int uid) {
        return userService.deleteUserById(uid);
    }

}
