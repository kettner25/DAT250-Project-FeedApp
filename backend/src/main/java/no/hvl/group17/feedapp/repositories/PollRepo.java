package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepo extends JpaRepository<Poll, Integer> {

}
