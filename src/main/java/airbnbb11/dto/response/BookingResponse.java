package airbnbb11.dto.response;

import airbnbb11.dto.request.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String price;
    private UserResponse userResponse;
}
