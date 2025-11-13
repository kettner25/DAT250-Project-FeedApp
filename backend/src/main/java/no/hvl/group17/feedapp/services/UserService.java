package no.hvl.group17.feedapp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    /**
     * Get all users
     * @return User collection
     * */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Get user by id
     * @param id id of user
     * @return User or null
     * */
    public User getUser(int id) {
        return userRepo.findById(id).orElse(null);
    }

    /**
     * Get user by username
     * @param username Username of user
     * @return User or null
     * */
    public User getUser(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    /**
     * Get user by email
     * @param email email of user
     * @return User or null
     * */
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    /**
     * Check if User params are unique for adding it to db
     * @param user User
     * @return Is unique?
     * */
    public Boolean isUnique(User user) {
        return userRepo.checkIfUnique(user.getUsername(), user.getEmail());
    }

    /**
     * Create new user and store into DB
     * @param user New user to create
     * @return created User or null if not created
     * */
    public User createUser(User user) {
        if (!isUnique(user)) return null;

        return userRepo.save(user);
    }

    /**
     * Edit user info
     * @param uid UserID of user that should be edited
     * @param user User to be edited
     * @return Edited user or null
     * */
    public User editUser(int uid, User user) {
        if (user.getId() == null) return null;

        // todo fix
        if (!user.getId().equals(uid)) return null;

        var dbUser = getUser(user.getId());

        if (dbUser == null) return null;

        //Email needs to be unique
        var emailUser = getUserByEmail(user.getEmail());
        if (emailUser != null && !Objects.equals(emailUser.getId(), user.getId())) return null;

        dbUser.setEmail(user.getEmail());

        return userRepo.save(dbUser);
    }

    /**
     * Delete user by id, if user does not exist return false
     * @param id ID of user to be deleted
     * @return Proceeded correctly?
     * */
    public Boolean deleteUserById(int id) {
        if (getUser(id) == null) return false;

        userRepo.deleteById(id);

        return true;
    }

    // todo comments
    @Transactional
    public User getOrCreateFromJwt(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        return userRepo.findByKeycloakId(keycloakId)
                .map(existing -> updateIfChanged(existing, username, email))
                .orElseGet(() -> createNewUser(keycloakId, username, email));
    }

    // todo comments
    private User createNewUser(String keycloakId, String username, String email) {
        User user = User.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .build();

        return userRepo.save(user);
    }

    // todo comments
    private User updateIfChanged(User user, String username, String email) {
        boolean changed = false;

        if (username != null && !username.equals(user.getUsername())) {
            user.setUsername(username);
            changed = true;
        }

        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            changed = true;
        }

        if (changed)
            return userRepo.save(user);
        return user;
    }

}
