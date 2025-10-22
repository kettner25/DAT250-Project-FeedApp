package no.hvl.group17.feedapp.domain;

import jakarta.persistence.*;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
@Data
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
    @ManyToOne
    private User user;
}
