package no.hvl.group17.feedapp.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        User created = userService.register(req.getUsername(), req.getEmail(), req.getPassword());
        if (created == null) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        }

        return ResponseEntity.ok("User created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        User user = userService.getUser(req.getUsername());
        if (user == null) return ResponseEntity.status(401).body("Invalid credentials");

        if (!encoder.matches(req.getPassword(), user.getPasswordHash()))
            return ResponseEntity.status(401).body("Invalid credentials");

        UserDetails springUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();

        String token = jwtUtil.generateToken(springUser);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "email", user.getEmail(),
                "id", user.getId()
        ));
    }
}

@Data
class LoginRequest {
    String username;
    String password;
}

@Data
class RegisterRequest {
    String username;
    String password;
    String email;
}
