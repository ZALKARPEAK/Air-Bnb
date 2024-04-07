package airbnbb11.entities;

import airbnbb11.entities.enums.Region;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String province;
    private String address;
    @Enumerated(EnumType.STRING)
    private Region region;
    private double longitude;
    private double latitude;
}