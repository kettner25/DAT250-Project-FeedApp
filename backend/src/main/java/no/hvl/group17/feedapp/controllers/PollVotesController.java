package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.services.PollService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polls/{pid}/votes")
public class PollVotesController {
    @Autowired
    private PollService pollService;

    @Autowired
    private VoteService voteService;

    /// Public
    @PostMapping("/")
    public Boolean vote(@PathVariable int pid, @RequestBody Vote vote) {
        if (!vote.Verify()) return false;

        return voteService.createVote(vote);
    }

    /// Public
    @DeleteMapping("/{vid}")
    public Boolean delete(@PathVariable int vid) {
        return voteService.deleteVote(vid);
    }
}
