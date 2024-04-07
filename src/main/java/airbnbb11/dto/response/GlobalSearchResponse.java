package airbnbb11.dto.response;

import airbnbb11.dto.house.HouseResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalSearchResponse {
    List<HouseSearchResponse> houseResponses;
}
