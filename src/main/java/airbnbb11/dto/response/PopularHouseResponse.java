package airbnbb11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PopularHouseResponse {
    private Long id;
    private String title;
    private List<String> images;
    private String address;
    private int price;
    private double rating;
}
