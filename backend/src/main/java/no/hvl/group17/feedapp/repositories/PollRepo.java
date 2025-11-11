package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PollRepo extends JpaRepository<Poll, Integer> {

    @Query("SELECT o FROM Option o where o.id = :id")
    Optional<Option> getOptionById(@Param("id") int id);
}
