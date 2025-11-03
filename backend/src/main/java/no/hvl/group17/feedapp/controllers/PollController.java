package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.services.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @GetMapping("/")
    public List<Poll> getAll() {
        throw new RuntimeException("Not Implemented");
    }

    @GetMapping("/{pid}")
    public Poll getPoll(@PathVariable int pid) {
        throw new RuntimeException("Not Implemented");
    }

    @PostMapping("/")
    public Poll createPoll(@RequestBody Poll poll) {
        throw new RuntimeException("Not Implemented");
    }

    @PutMapping("/{pid}")
    public Poll editPoll(@PathVariable int pid, @RequestBody Poll poll) {
        throw new RuntimeException("Not Implemented");
    }

    @DeleteMapping("/{pid}")
    public Boolean deletePoll(@PathVariable int pid) {
        throw new RuntimeException("Not Implemented");
    }

    @GetMapping("/{pid}/count")
    public int getVoteCount(@PathVariable int pid) {
        throw new RuntimeException("Not Implemented");
    }
}
