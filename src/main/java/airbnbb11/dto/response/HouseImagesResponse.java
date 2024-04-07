package airbnbb11.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HouseImagesResponse {
    private List<String> images;
}
