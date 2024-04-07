package airbnbb11.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favourite_gen")
    @SequenceGenerator(name = "favourite_gen", sequenceName = "favourite_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private LocalDate createdDate;
    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH})
    private House house;
    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH})
    private User user;
}