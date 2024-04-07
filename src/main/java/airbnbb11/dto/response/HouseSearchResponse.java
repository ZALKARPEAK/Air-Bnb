package airbnbb11.dto.response;

import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseSearchResponse {
    private Long id;
    private List<String> images;
    private String houseName;
    private String title;
    private HouseType houseType;
    private int price;
    private String address;
    @Enumerated(EnumType.STRING)
    private HouseStatus houseStatus;
    private int maxGuests;
    private double rating;
    private String description;
    private Region region;
    private String province;

}
