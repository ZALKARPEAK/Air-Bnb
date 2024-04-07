package airbnbb11.dto.house;

import airbnbb11.entities.enums.HouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllHousesResponse {
    private Long id;
    private HouseType houseType;
    private List<String> images;
    private BigDecimal price;
    private String region;
    private String address;
    private String description;
    private String status;
    private String title;
    private int maxGuests;
    private String province;
    private double rating;


}
