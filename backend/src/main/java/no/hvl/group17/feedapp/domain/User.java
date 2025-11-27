package no.hvl.group17.feedapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")   // kein nullable = false !
    private String passwordHash;

    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference("user-polls")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Poll> polls = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference("user-votes")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    public Boolean Verify() {
        if (username == null || username.isEmpty()) return false;
        if (email == null || email.isEmpty()) return false;
        return passwordHash != null && !passwordHash.isEmpty();
    }
}
