package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepo extends JpaRepository<Vote, Integer> {

}
