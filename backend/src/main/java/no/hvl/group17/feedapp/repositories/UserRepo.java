package no.hvl.group17.feedapp.repositories;

import no.hvl.group17.feedapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
    Optional<User> findByKeycloakId(@Param("keycloakId") String keycloakId);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) = 0 FROM User u WHERE u.email = :email or u.username = :username")
    Boolean checkIfUnique(@Param("username") String username, @Param("email") String email);
}
