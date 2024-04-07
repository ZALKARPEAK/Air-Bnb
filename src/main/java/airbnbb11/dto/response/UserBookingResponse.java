package airbnbb11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBookingResponse {
    private Long id;
    private List<String> images;
    private int price;
    private Double rating;
    private String title;
    private String description;
    private String address;
    private Integer maxGuests;
    private String region;
    private String checkIn;
    private String checkOut;
}
