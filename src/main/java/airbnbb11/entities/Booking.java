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
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_gen")
    @SequenceGenerator(name = "booking_gen", sequenceName = "booking_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    @ManyToOne(cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE})
    private User user;
    @ManyToOne
    private House house;
}