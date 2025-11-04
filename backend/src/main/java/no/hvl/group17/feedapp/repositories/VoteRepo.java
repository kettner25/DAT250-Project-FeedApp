package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepo extends JpaRepository<Vote, Integer> {

    @Query("SELECT o.id, COUNT(v) FROM Option o LEFT JOIN o.votes v WHERE o.poll.id = :pid GROUP BY o.id")
    List<Object[]> getVoteCountByPoll(@Param("pid") int id);
}
