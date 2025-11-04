package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

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
     * @param user User to be edited
     * @return Edited user or null
     * */
    public User editUser(User user) {
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
        if (getUser(id) != null) return false;

        userRepo.deleteById(id);

        return true;
    }
}
