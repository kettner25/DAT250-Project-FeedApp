package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.Vote;
import no.hvl.group17.feedapp.models.OptionCount;
import no.hvl.group17.feedapp.repositories.VoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteService {
    @Autowired
    private VoteRepo voteRepo;

    @Autowired
    private PollService pollService;

    @Autowired
    private UserService userService;

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
    public List<OptionCount> getVoteCountsByPoll(int id) {
        var poll = pollService.getPollById(id);

        if (poll == null) return List.of();

        var list = voteRepo.getVoteCountByPoll(id);

        List<OptionCount> res = new ArrayList<>();
        for (Object[] item : list) {
            poll.getOptions().stream().filter(option -> option.getId() == (int) item[0]).findFirst().ifPresent(option -> {
                res.add(new OptionCount(option, (int)(long)item[1]));
            });
        }

        return res;
    }

    /**
     * Vote on spec. option
     * @param vote Vote
     * @return if proceeded correctly
     * */
    public Boolean createVote(Vote vote) {
        if (vote.getUser() != null) {
            if (userService.getUser(vote.getUser().getId()) == null) return false;
            vote.setAnonId(null);

            if (voteRepo.existsVoteByUserID(vote.getUser().getId(), vote.getOption().getId())) return false;
        }
        else {
            if (vote.getAnonId() == null || vote.getAnonId().isEmpty()) return false;

            if (voteRepo.existsVoteByAnonID(vote.getAnonId(), vote.getOption().getId())) return false;
        }

        if (pollService.getOptionById(vote.getOption().getId()) == null) return false;

        voteRepo.save(vote);

        return true;
    }

    /**
     * Unvote on spec. option
     * @param id Vote id
     * @return if proceeded correctly
     * */
    public Boolean deleteVote(int id) {
        if (getVoteById(id) == null) return false;

        voteRepo.deleteById(id);

        return true;
    }
}
