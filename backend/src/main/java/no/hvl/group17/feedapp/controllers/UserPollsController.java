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

    /// ADMIN or USER.id = uid
    @GetMapping("/")
    public List<Poll> getAll(@PathVariable int uid) {
        return pollService.getAllPollsByUser(uid);
    }

    /// ADMIN or USER.id = uid
    @GetMapping("/{pid}")
    public Poll getPoll(@PathVariable int uid, @PathVariable int pid) {
        return pollService.getPollByIdByUser(uid, pid);
    }

    /// ADMIN or USER.id = uid
    @PostMapping("/")
    public Poll createPoll(@PathVariable int uid, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        return pollService.createPoll(poll);
    }

    /// ADMIN or USER.id = uid
    @PutMapping("/{pid}")
    public Poll editPoll(@PathVariable int uid, @PathVariable int pid, @RequestBody Poll poll) {
        if (!poll.Verify()) return null;

        if (poll.getId() != pid) return null;

        return pollService.editPoll(poll);
    }

    /// ADMIN or USER.id = uid
    @DeleteMapping("/{pid}")
    public Boolean deletePoll(@PathVariable int uid, @PathVariable int pid) {
        if (pollService.getPollByIdByUser(uid, pid) == null) return false;

        return pollService.deletePollById(pid);
    }
}
