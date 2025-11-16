package no.hvl.group17.feedapp.services;

import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.repositories.VoteRepo;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate; 
import org.springframework.cache.annotation.Cacheable;                                                                                              
import org.springframework.cache.annotation.CacheEvict;   

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepo voteRepo;
    private final PollService pollService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Get vote by its ID
     * @param id  Vote ID
     * @return Vote
     * */
    public Vote getVoteById(int id) {
        return voteRepo.findById(id).orElse(null);
    }

    /**
     * Get count of votes for each option by Poll
     * @param id Poll id
     * @return List of {Option, count}
     * */
    @Cacheable(value = "voteCounts", key = "#id")
    public List<OptionCount> getVoteCountsByPoll(int id) {
        var poll = pollService.getPollById(id);

        if (poll == null) return List.of();

        var list = voteRepo.getVoteCountByPoll(id);

        List<OptionCount> res = new ArrayList<>();
        for (Object[] item : list) {
            poll.getOptions().stream().filter(option ->
                    option.getId() == (int) item[0]).findFirst().ifPresent(option ->
                    res.add(new OptionCount(option, (int)(long)item[1])));
        }

        return res;
    }

    /**
     * Vote on spec. option
     * @param uid ID of the user
     * @param aid ID from cookie for anonymouse voting
     * @param pid Poll id
     * @param vote Vote
     * @return if proceeded correctly
     * */
    @CacheEvict(value = "voteCounts", allEntries = true)
    public Vote createVote(int uid, String aid, int pid, Vote vote) {
        if (vote == null) return null;

        if (uid > 0) {
            if (userService.getUser(uid) == null) return null;
            vote.setUser(userService.getUser(uid));
            vote.setAnonId(null);
            if (voteRepo.existsVoteByUserID(vote.getUser().getId(), vote.getOption().getId())) return null;
        }
        else if (aid != null && !aid.isEmpty()) {
            vote.setUser(null);
            vote.setAnonId(aid);
            if (voteRepo.existsVoteByAnonID(vote.getAnonId(), vote.getOption().getId())) return null;
        }
        else {
            return null;
        }

        var option = pollService.getOptionById(vote.getOption().getId());
        if (option == null) return null;
        var pollId = option.getPoll().getId();
        if (!pollId.equals(pid)) return null;

        vote = voteRepo.save(vote);

        rabbitTemplate.convertAndSend("vote-events", "vote-created:" + vote.getId());
        rabbitTemplate.convertAndSend("vote-events", "vote-created-data:" + vote);
        rabbitTemplate.convertAndSend("poll-events", "poll-updated:" + pollId);

        return vote;
    }


    /**
     * Unvote on spec. option
     * @param uid ID of the user
     * @param aid ID from cookie for anonymouse voting
     * @param pid Poll id
     * @param vid Vote id
     * @return if proceeded correctly
     * */
    @CacheEvict(value = "voteCounts", key = "#pid")
    public Boolean deleteVote(int uid, String aid, int pid, int vid) {
        var vote = getVoteById(vid);
        if (vote == null) return false;

        var pollId = vote.getOption().getPoll().getId();
        if (pollId != pid) return false;

        boolean hasUser = uid > 0;
        boolean hasAnonId = aid != null && !aid.isEmpty();

        if (!hasUser && !hasAnonId) {
            // No identity information provided

            return false;
        }

        if ((hasUser && uid == vote.getUser().getId()) || (hasAnonId && aid.equals(vote.getAnonId()))) {
            // Valid authorized user or valid anonymous

            voteRepo.deleteById(vid);

            rabbitTemplate.convertAndSend("vote-events", "vote-deleted:" + vid);
            rabbitTemplate.convertAndSend("poll-events", "poll-updated:" + pollId);

            return true;
        }

        return false;
    }

}
