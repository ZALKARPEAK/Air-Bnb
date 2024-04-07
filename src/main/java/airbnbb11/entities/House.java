package airbnbb11.entities;

import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "house_gen")
    @SequenceGenerator(name = "house_gen", sequenceName = "house_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String houseName;
    private String title;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    private HouseType houseType;
    private LocalDate createdDate;
    private LocalDateTime createdTime;
    private int price;
    @ElementCollection
    @CollectionTable(name = "house_images", joinColumns = @JoinColumn(name = "house_id"))
    @Column(name = "image")
    private List<String> images;
    private int maxGuests;
    @Enumerated(EnumType.STRING)
    private HouseStatus houseStatus;
    private String messageFromAdmin;
    @ManyToOne
    private User user;
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
    @OneToMany(mappedBy = "house", cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.REMOVE})
    private List<Booking> bookings;
    @OneToMany(mappedBy = "house", cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.REMOVE},fetch = FetchType.EAGER)
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "house", cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.REMOVE})
    private List<Favourite> favourites;
}