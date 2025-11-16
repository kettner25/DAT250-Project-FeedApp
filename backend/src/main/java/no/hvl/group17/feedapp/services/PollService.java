package no.hvl.group17.feedapp.services;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.repositories.PollRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepo pollRepo;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

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
     * @param uid  ID of the user
     * @param poll Poll to be created
     * @return Created poll
     * */
    public Poll createPoll(int uid, Poll poll) {
        if (poll == null) return null;

        if (uid != poll.getUser().getId()) return null;

        poll = pollRepo.save(poll);

        rabbitTemplate.convertAndSend("poll-events", "poll-created:" + poll.getId());
        rabbitTemplate.convertAndSend("poll-events", "poll-created-data:" + poll);

        return poll;
    }

    /**
     * Edit poll
     * @param uid  User id
     * @param pid  Poll id to be edited
     * @param poll Poll to be edited
     * @return Edited poll or Null
     * */
    public Poll editPoll(int uid, int pid, Poll poll) {
        if (poll == null) return null;
        if (poll.getId() == null) return null;

        if (uid != poll.getUser().getId()) return null;
        if (pid != poll.getId()) return null;

        var dbPoll = getPollById(poll.getId());

        if (dbPoll == null) return null;

        //Set allowed poll params
        dbPoll.setValidUntil(poll.getValidUntil());
        poll.getOptions().forEach((option) ->
                dbPoll.getOptions().stream().filter(option2 ->
                        Objects.equals(option2.getId(), option.getId())).findFirst().ifPresentOrElse(option2 ->
                        option2.setOrder(option.getOrder()), () ->
                        dbPoll.getOptions().add(option)));

        poll = pollRepo.save(dbPoll);

        rabbitTemplate.convertAndSend("poll-events", "poll-updated:" + poll.getId());

        return poll;
    }

    /**
     * Delete poll by its ID
     * @param uid ID of user
     * @param pid ID of poll
     * @return if proceeded correctly
     * */
    public Boolean deletePollById(int uid, int pid) {
        if (getPollById(pid) == null) return false;
        if (getPollById(pid).getUser().getId() != uid) return false;

        pollRepo.deleteById(pid);

        rabbitTemplate.convertAndSend("poll-events", "poll-deleted:" + pid);

        return true;
    }
}
