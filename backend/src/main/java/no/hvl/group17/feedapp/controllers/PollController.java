package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.services.PollService;
import no.hvl.group17.feedapp.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private VoteService voteService;

    /// Public
    @GetMapping("/")
    public List<Poll> getAll() {
        return pollService.getAllPolls();
    }

    /// Public
    @GetMapping("/{pid}")
    public Poll getPoll(@PathVariable int pid) {
        return pollService.getPollById(pid);
    }

    /// ADMIN
    @PostMapping("/")
    public Poll createPoll(@RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        return pollService.createPoll(poll);
    }

    /// ADMIN
    @PutMapping("/{pid}")
    public Poll editPoll(@PathVariable int pid, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        return pollService.editPoll(poll);
    }

    /// ADMIN
    @DeleteMapping("/{pid}")
    public Boolean deletePoll(@PathVariable int pid) {
        return pollService.deletePollById(pid);
    }

    /// Public
    @GetMapping("/{pid}/count")
    public List<OptionCount> getVoteCount(@PathVariable int pid) {
        return voteService.getVoteCountsByPoll(pid);
    }
}
