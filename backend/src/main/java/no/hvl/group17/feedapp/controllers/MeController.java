package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.UserService;
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
    public User getMe(@AuthenticationPrincipal Jwt jwt) {
        return userService.getOrCreateFromJwt(jwt);
    }

    /// Authenticated USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/polls")
    public List<Poll> getMyPolls(@AuthenticationPrincipal Jwt jwt) {
        return userService.getOrCreateFromJwt(jwt).getPolls();
    }

    /// Authenticated USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/votes")
    public List<Vote> getMyVotes(@AuthenticationPrincipal Jwt jwt) {
        return userService.getOrCreateFromJwt(jwt).getVotes();
    }

}
