package airbnbb11.dto.mappers;

import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.entities.House;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Function;


@Service
public class HouseMapper implements Function<House, AllHousesResponse> {

    @Override
    public AllHousesResponse apply(House house) {
        return AllHousesResponse.builder()
                .id(house.getId())
                .houseType(house.getHouseType())
                .images(house.getImages())
                .price(BigDecimal.valueOf(house.getPrice()))
                .region(house.getAddress().getRegion().name())
                .address(house.getAddress().getAddress())
                .description(house.getDescription())
                .status(house.getHouseStatus().name())
                .title(house.getTitle())
                .maxGuests(house.getMaxGuests())
                .province(house.getAddress().getProvince())
                .rating((long) house.getFeedbacks().size())
                .build();
    }
}
