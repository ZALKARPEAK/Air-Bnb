package airbnbb11.dto.response;

import airbnbb11.entities.enums.HouseStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteHousesResponse {
    private Long id;
    private List<String> images;
    private int price;
    private String address;
    private String description;
    @Enumerated(EnumType.STRING)
    private HouseStatus houseStatus;
    private int maxGuests;
    private double rating;
    private boolean isFavorite;
}