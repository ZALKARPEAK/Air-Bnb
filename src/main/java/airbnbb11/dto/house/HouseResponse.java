package airbnbb11.dto.house;

import airbnbb11.dto.request.UserResponse;
import airbnbb11.entities.House;
import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HouseResponse {

    private Long id;
    private String title;
    private String description;
    private HouseType houseType;
    private LocalDate createdDate;
    private BigDecimal price;
    private List<String> images;
    private int maxGuests;
    private HouseStatus houseStatus;
    private String address;
    private String province;
    private UserResponse userResponse;
    private boolean booked;
    private boolean favorite;

    public HouseResponse(House house, String address, String province, boolean booked, boolean favorite) {
        this.id = house.getId();
        this.title = house.getTitle();
        this.description = house.getDescription();
        this.houseType = house.getHouseType();
        this.createdDate = house.getCreatedDate();
        this.price = BigDecimal.valueOf(house.getPrice());
        this.images = house.getImages();
        this.maxGuests = house.getMaxGuests();
        this.houseStatus = house.getHouseStatus();
        this.address = address;
        this.province = province;
        this.booked=booked;
        this.favorite =favorite;
        this.userResponse = new UserResponse(house.getUser().getId(),
                house.getUser().getFirstName() +" " +
                        " "+house.getUser().getLastName(),
                house.getUser().getEmail(),
                house.getUser().getImage());
    }
}
