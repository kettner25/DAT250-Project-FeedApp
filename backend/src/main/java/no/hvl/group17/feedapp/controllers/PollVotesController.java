package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.services.VoteService;
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
    public Vote vote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable int pid,
            @RequestBody Vote vote,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        if (!vote.Verify()) return null;
        // todo fix
        int uid = userService.getOrCreateFromJwt(jwt).getId();
        return voteService.createVote(uid, anonId, pid, vote);
    }

    /// Public
    @DeleteMapping("/{oid}")
    public Boolean unvote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable int pid,
            @PathVariable int oid,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        // todo fix
        int uid = userService.getOrCreateFromJwt(jwt).getId();
        return voteService.deleteVote(uid, anonId, pid, oid);
    }
}
