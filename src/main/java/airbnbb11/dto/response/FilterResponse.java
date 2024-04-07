package airbnbb11.dto.response;

import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class FilterResponse {
    private Long id;
    private HouseType houseType;
    private List<String> images;
    private int price;
    private Region region;
    private String address;
    private String description;
    @Enumerated(EnumType.STRING)
    private HouseStatus houseStatus;
    private String title;
    private int maxGuests;
    private String province;
    private double rating;

    public FilterResponse(Long id, HouseType houseType, List<String> images, int price, Region region, String address, String description, HouseStatus houseStatus, String title, int maxGuests, String province, double rating) {
        this.id = id;
        this.houseType = houseType;
        this.images = images;
        this.price = price;
        this.region = region;
        this.address = address;
        this.description = description;
        this.houseStatus = houseStatus;
        this.title = title;
        this.maxGuests = maxGuests;
        this.province = province;
        this.rating = rating;
    }

}
