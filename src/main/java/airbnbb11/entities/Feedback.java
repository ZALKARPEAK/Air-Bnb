package airbnbb11.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_gen")
    @SequenceGenerator(name = "feedback_gen", sequenceName = "feedback_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    @Column(length = 1000)
    private String feedback;
    private int rating;
    @ElementCollection
    @CollectionTable(name = "feedback_images", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "image")
    private List<String> images;
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> likes;
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> dislikes;
    private LocalDate postedAt;
    @ManyToOne
    private User user;
    @ManyToOne
    private House house;

    public Feedback(House house, User user, String feedback, int rating, List<String> images, LocalDate now) {
        this.house = house;
        this.user = user;
        this.feedback = feedback;
        this.rating = rating;
        this.images = images;
        this.postedAt = now;
    }
}