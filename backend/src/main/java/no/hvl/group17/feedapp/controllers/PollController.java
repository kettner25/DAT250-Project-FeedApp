package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.services.PollService;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;
    private final VoteService voteService;
    private final UserService userService;

    /// Public
    @GetMapping("")
    public List<Poll> getAll() {
        return pollService.getAllPolls();
    }

    /// Public
    @GetMapping("/{pid}")
    public Poll getPoll(@PathVariable int pid) {
        return pollService.getPollById(pid);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    public Poll createPoll(@AuthenticationPrincipal Jwt jwt, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (jwt.getClaimAsStringList("roles").contains("ADMIN"))
            uid = poll.getUser().getId();

        return pollService.createPoll(uid, poll);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{pid}")
    public Poll editPoll(@AuthenticationPrincipal Jwt jwt, @PathVariable int pid, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (jwt.getClaimAsStringList("roles").contains("ADMIN"))
            uid = poll.getUser().getId();

        return pollService.editPoll(uid, pid, poll);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{pid}")
    public Boolean deletePoll(@AuthenticationPrincipal Jwt jwt, @PathVariable int pid) {
        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (jwt.getClaimAsStringList("roles").contains("ADMIN"))
            uid = pollService.getPollById(pid).getUser().getId();

        return pollService.deletePollById(uid, pid);
    }

    /// Public
    @GetMapping("/{pid}/count")
    public List<OptionCount> getVoteCount(@PathVariable int pid) {
        return voteService.getVoteCountsByPoll(pid);
    }
}
