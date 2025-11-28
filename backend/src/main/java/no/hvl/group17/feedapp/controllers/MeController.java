package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    /// Authenticated USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal Jwt jwt) {
        var res = userService.getOrCreateFromJwt(jwt);
        if (res == null) return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /// Authenticated USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/polls")
    public ResponseEntity<List<Poll>> getMyPolls(@AuthenticationPrincipal Jwt jwt) {
        var res = userService.getOrCreateFromJwt(jwt).getPolls();

        if (res.isEmpty()) return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /// Authenticated USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getMyVotes(@AuthenticationPrincipal Jwt jwt) {
        var res = userService.getOrCreateFromJwt(jwt).getVotes();

        if (res.isEmpty()) return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
