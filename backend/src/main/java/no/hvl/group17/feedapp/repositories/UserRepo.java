package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {

}
