package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.repositories.PollRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PollService {
    @Autowired
    private PollRepo pollRepo;

    @Autowired
    private UserService userService;

    /**
     * Get all polls
     * @return Polls collection
     * */
    public List<Poll> getAllPolls() {
        return pollRepo.findAll();
    }

    /**
     * Get poll by id
     * @param id ID
     * @return Poll or null
     * */
    public Poll getPollById(int id) {
        return pollRepo.findById(id).orElse(null);
    }

    /**
     * Get option by id
     * @param id ID
     * @return Option or null
     * */
    public Option getOptionById(int id) {
        return pollRepo.getOptionById(id).orElse(null);
    }

    /**
     * Get all polls by user ID
     * @param id User id
     * @return Polls collection
     * */
    public List<Poll> getAllPollsByUser(int id) {
        var user = userService.getUser(id);

        if (user == null || user.getPolls() == null) return List.of();

        return user.getPolls();
    }

    /**
     * Get poll by id and verify that owner is user by uid
     * @param uid User ID
     * @param pid Poll ID
     * @return Poll or null if not found
     * */
    public Poll getPollByIdByUser(int uid, int pid) {
        var poll = getPollById(pid);

        if (poll.getUser().getId() == uid) return poll;

        return null;
    }

    /**
     * Create poll
     * @param poll Poll to be created
     * @return Created poll
     * */
    public Poll createPoll(Poll poll) {
        return pollRepo.save(poll);
    }

    /**
     * Edit poll
     * @param poll Poll to be edited
     * @return Edited poll or Null
     * */
    public Poll editPoll(Poll poll) {
        if (poll.getId() == null) return null;

        var dbPoll = getPollById(poll.getId());

        if (dbPoll == null) return  null;

        //Set allowed poll params
        dbPoll.setValidUntil(poll.getValidUntil());
        poll.getOptions().forEach((option) -> {
           dbPoll.getOptions().stream().filter(option2 ->
                   Objects.equals(option2.getId(), option.getId())).findFirst().ifPresentOrElse(option2 -> {
                      option2.setOrder(option.getOrder());
                   }, () -> {
                       dbPoll.getOptions().add(option);
                   });
        });

        return pollRepo.save(dbPoll);
    }

    /**
     * Delete poll by its ID
     * @param id ID of poll
     * @return if proceeded correctly
     * */
    public Boolean deletePollById(int id) {
        if (getPollById(id) == null) return false;

        pollRepo.deleteById(id);

        return true;
    }
}
