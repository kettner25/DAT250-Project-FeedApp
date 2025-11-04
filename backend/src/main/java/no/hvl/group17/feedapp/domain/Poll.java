package no.hvl.group17.feedapp.domain;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    @Builder.Default
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    public Boolean Verify() {
        if (question == null || question.isEmpty()) return false;

        if (options == null || options.isEmpty() || options.size() < 2) return false;

        return user != null;
    }
}
