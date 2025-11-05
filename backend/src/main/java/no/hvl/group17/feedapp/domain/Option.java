package no.hvl.group17.feedapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poll_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String caption;
    @Column(name = "porder")
    private Integer order;

    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    @JoinColumn(nullable = false)
    private Poll poll;

    @Builder.Default
    @ToString.Exclude
    @JsonBackReference
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    public Boolean Verify() {
        if (caption == null || caption.isEmpty()) return false;

        if (poll == null) return false;

        return order == null || order >= 0;
    }
}
