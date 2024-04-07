package airbnbb11.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRequest {
    @Min(1)
    private double amount;
    private Long houseId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long bookingId;
}
