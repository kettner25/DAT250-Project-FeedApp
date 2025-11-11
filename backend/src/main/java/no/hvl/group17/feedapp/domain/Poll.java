package no.hvl.group17.feedapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import lombok.*;

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
    @ToString.Exclude
    @JsonManagedReference("poll-options")
    @OneToMany(mappedBy = "poll", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
    @ManyToOne
    @ToString.Exclude
    @JsonBackReference("user-polls")
    @JoinColumn(nullable = false)
    private User user;

    public Boolean Verify() {
        if (question == null || question.isEmpty()) return false;

        if (options == null || options.isEmpty() || options.size() < 2) return false;

        return user != null;
    }

    public void linkOptions() {
        if (options != null)
            options.forEach(o -> o.setPoll(this));
    }
}
