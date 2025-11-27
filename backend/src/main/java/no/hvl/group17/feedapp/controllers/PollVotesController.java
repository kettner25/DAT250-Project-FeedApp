package no.hvl.group17.feedapp.controllers;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.UserService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int pid,
            @RequestBody Vote vote,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        vote.setAnonId(anonId);

        if (!vote.Verify()) return null;

        int uid = (userDetails == null)
                ? -1                                          // anonymer User
                : userService.getByUsername(userDetails.getUsername()).getId();

        return voteService.createVote(uid, anonId, pid, vote);
    }

    /// Public
    @DeleteMapping("/{vid}")
    public Boolean unvote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int pid,
            @PathVariable int vid,
            @CookieValue(value = "anonId", required = false) String anonId
    ) {
        int uid = (userDetails == null)
                ? -1
                : userService.getByUsername(userDetails.getUsername()).getId();

        return voteService.deleteVote(uid, anonId, pid, vid);
    }
}
