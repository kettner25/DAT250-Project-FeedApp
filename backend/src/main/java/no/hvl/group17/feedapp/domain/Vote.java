package no.hvl.group17.feedapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(unique = true)
    private String anonId;
    private Instant publishedAt;

    @ManyToOne
    @ToString.Exclude
    @JsonManagedReference
    @JoinColumn(nullable = false)
    private Option option;
    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    private User user;

    public Boolean Verify() {
        if ((anonId == null || anonId.isEmpty()) && user == null) return false;

        return option != null;
    }
}
