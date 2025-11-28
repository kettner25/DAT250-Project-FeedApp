package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.services.PollService;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Poll> getPoll(@PathVariable int pid) {
        var poll = pollService.getPollById(pid);

        if (poll == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new  ResponseEntity<>(poll, HttpStatus.OK);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("")
    public ResponseEntity<Poll> createPoll(@AuthenticationPrincipal Jwt jwt, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (hasAdminRole(jwt))
            uid = poll.getUser().getId();

        var _poll = pollService.createPoll(uid, poll);
        return new ResponseEntity<>(_poll, _poll != null ? HttpStatus.CREATED :  HttpStatus.BAD_REQUEST);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{pid}")
    public ResponseEntity<Poll> editPoll(@AuthenticationPrincipal Jwt jwt, @PathVariable int pid, @RequestBody Poll poll) {
        if (!poll.Verify()) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (hasAdminRole(jwt))
            uid = poll.getUser().getId();

        var _poll = pollService.editPoll(uid, pid, poll);
        return new ResponseEntity<>(_poll, _poll != null ? HttpStatus.OK :  HttpStatus.BAD_REQUEST);
    }

    /// Authenticated USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{pid}")
    public ResponseEntity<Boolean> deletePoll(@AuthenticationPrincipal Jwt jwt, @PathVariable int pid) {
        int uid = userService.getOrCreateFromJwt(jwt).getId();
        if (hasAdminRole(jwt))
            uid = pollService.getPollById(pid).getUser().getId();

        var bool = pollService.deletePollById(uid, pid);
        return new ResponseEntity<>(bool, bool ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }

    /// Public
    @GetMapping("/{pid}/count")
    public ResponseEntity<List<OptionCount>> getVoteCount(@PathVariable int pid) {
        var res = voteService.getVoteCountsByPoll(pid);

        if (res.isEmpty())  return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /// Private helper
    private static boolean hasAdminRole(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realm)) return false;
        Object rolesObj = realm.get("roles");
        if (!(rolesObj instanceof List<?> roles)) return false;
        return roles.contains("ADMIN");
    }
}
