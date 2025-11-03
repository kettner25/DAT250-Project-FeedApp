package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.services.PollService;
import no.hvl.group17.feedapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{uid}/polls")
public class UserPollsController {
    @Autowired
    private UserService userService;

    @Autowired
    private PollService pollService;

    @GetMapping("/")
    public List<Poll> getAll(@PathVariable int uid) {
        throw new RuntimeException("Not Implemented");
    }

    @GetMapping("/{pid}")
    public Poll getPoll(@PathVariable int uid, @PathVariable int pid) {
        throw new RuntimeException("Not Implemented");
    }

    @PostMapping("/")
    public Poll createPoll(@PathVariable int uid, @RequestBody Poll poll) {
        throw new RuntimeException("Not Implemented");
    }

    @PutMapping("/{pid}")
    public Poll editPoll(@PathVariable int uid, @PathVariable int pid, @RequestBody Poll poll) {
        throw new RuntimeException("Not Implemented");
    }

    @DeleteMapping("/{pid}")
    public Boolean deletePoll(@PathVariable int uid, @PathVariable int pid) {
        throw new RuntimeException("Not Implemented");
    }
}
