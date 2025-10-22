package no.hvl.group17.feedapp.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "votes")
@Data
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String anonId;
    private Instant publishedAt;

    @ManyToOne
    private Option option;
    @ManyToOne
    private User user;
}
