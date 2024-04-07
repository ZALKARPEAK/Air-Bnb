package airbnbb11.dto.house;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
public class PagHouseResponse {

    private Long id;
    private HouseType houseType;
    private List<String> images;
    private int price;
    private Region region;
    private String address;
    private String description;
    private String title;
    private int maxGuests;
    private String province;
    private double rating;
    private boolean isFavorite;
    private boolean booked;

    public PagHouseResponse(Long id, HouseType houseType, List<String> images, int price, Region region, String address, String description, String title, int maxGuests, String province, double rating, boolean isFavorite, boolean booked) {
        this.id = id;
        this.houseType = houseType;
        this.images = images;
        this.price = price;
        this.region = region;
        this.address = address;
        this.description = description;
        this.title = title;
        this.maxGuests = maxGuests;
        this.province = province;
        this.rating = rating;
        this.isFavorite = isFavorite;
        this.booked=booked;
    }
}
