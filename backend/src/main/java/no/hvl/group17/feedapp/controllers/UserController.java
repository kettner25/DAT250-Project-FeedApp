package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<User>> getAll() {
        var users = userService.getAllUsers();

        if (users.isEmpty()) return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{uid}")
    public ResponseEntity<User> getUser(@PathVariable int uid) {
        var user = userService.getUser(uid);

        if (user == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uid}")
    public ResponseEntity<User> editUser(@PathVariable int uid, @RequestBody User user) {
        if (!user.Verify()) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        var _user = userService.editUser(uid, user);

        if (_user == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(_user, HttpStatus.OK);
    }

    /// ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uid}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable int uid) {
        var bool = userService.deleteUserById(uid);

        if (!bool) return new ResponseEntity<>(bool, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(bool, HttpStatus.NO_CONTENT);
    }

}
