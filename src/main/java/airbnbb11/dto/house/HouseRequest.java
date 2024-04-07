package airbnbb11.dto.house;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public record HouseRequest(
        String title,
        String description,
        @Enumerated(EnumType.STRING)
        HouseType houseType,
        int price,
        int maxOfGuests,
        Region region,
        String province,
        String address,
        List<String> images
) {
}
