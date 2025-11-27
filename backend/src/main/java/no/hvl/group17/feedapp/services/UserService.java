package no.hvl.group17.feedapp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User getUser(int id) {
        return userRepo.findById(id).orElse(null);
    }

    public User getUser(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    public User getByUsername(String username) {
    return userRepo.findByUsername(username).orElse(null);
}


    public Boolean isUnique(User user) {
        return userRepo.checkIfUnique(user.getUsername(), user.getEmail());
    }

    public User register(String username, String email, String password) {
        if (!userRepo.checkIfUnique(username, email))
            return null;

        User u = User.builder()
                .username(username)
                .email(email)
                .passwordHash(encoder.encode(password))
                .build();

        return userRepo.save(u);
    }

    public boolean validatePassword(User user, String rawPassword) {
        return encoder.matches(rawPassword, user.getPasswordHash());
    }

    public User editUser(int uid, User user) {
        if (user.getId() == null) return null;
        if (uid != user.getId()) return null;

        var dbUser = getUser(user.getId());
        if (dbUser == null) return null;

        var emailUser = getUserByEmail(user.getEmail());
        if (emailUser != null && !Objects.equals(emailUser.getId(), user.getId()))
            return null;

        dbUser.setEmail(user.getEmail());

        return userRepo.save(dbUser);
    }

    public Boolean deleteUserById(int id) {
        if (getUser(id) == null) return false;
        userRepo.deleteById(id);
        return true;
    }
}
