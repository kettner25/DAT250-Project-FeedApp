package no.hvl.group17.feedapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "votes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String anonId;  // if marked unique then only one anonymous user can vote only on one poll
    private Instant publishedAt;

    @ManyToOne
    @ToString.Exclude
    @JsonBackReference("option-votes")
    @JoinColumn(nullable = false)
    private Option option;
    @ManyToOne
    @ToString.Exclude
    @JsonBackReference("user-votes")
    private User user;

    @JsonProperty("userId")
    public Integer getUserId() {
        return user != null ? user.getId() : null;
    }

    public Boolean Verify() {
        if ((anonId == null || anonId.isEmpty()) && user == null) return false;

        return option != null;
    }
}
