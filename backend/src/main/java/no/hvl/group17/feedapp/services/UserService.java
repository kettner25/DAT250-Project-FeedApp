package no.hvl.group17.feedapp.services;

import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public User getUserById(int id) {
        return userRepo.findById(id).orElse(null);
    }

    /**
     * Create new user and store into DB
     * @param user New user to create
     * @return created User
     * */
    public User createUser(User user) {
        return userRepo.save(user);
    }

    /**
     * Edit user info
     * @param user User to be edited
     * @return Edited user
     * */
    public User editUser(User user) {
        var dbUser = getUserById(user.getId());

        dbUser.setEmail(user.getEmail());

        return userRepo.save(dbUser);
    }

    /**
     * Delete user by id, if user does not exist return false
     * @param id Id of user to be deleted
     * @return Proceeded correctly?
     * */
    public Boolean deleteUserById(int id) {
        if (getUserById(id) != null) return false;

        userRepo.deleteById(id);

        return true;
    }
}
