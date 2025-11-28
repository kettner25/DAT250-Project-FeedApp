package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polls/{pid}/votes")
@RequiredArgsConstructor
public class PollVotesController {

    private final VoteService voteService;
    private final UserService userService;

    /// Public
    @PostMapping("")
    public ResponseEntity<Vote> vote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable int pid,
            @RequestBody Vote vote,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        vote.setAnonId(anonId);

        if (!vote.Verify()) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        int uid = jwt == null ? -1 : userService.getOrCreateFromJwt(jwt).getId();

        var _vote = voteService.createVote(uid, anonId, pid, vote);
        if (_vote == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(_vote, HttpStatus.CREATED);
    }

    /// Public
    @DeleteMapping("/{vid}")
    public ResponseEntity<Boolean> unvote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable int pid,
            @PathVariable int vid,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        int uid = jwt == null ? -1 : userService.getOrCreateFromJwt(jwt).getId();

        var bool = voteService.deleteVote(uid, anonId, pid, vid);

        return new ResponseEntity<>(bool, bool ? HttpStatus.NO_CONTENT :  HttpStatus.BAD_REQUEST);
    }
}
