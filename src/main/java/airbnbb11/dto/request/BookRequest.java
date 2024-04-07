package airbnbb11.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    private Long id;
    @Min(1)
    private double amount;
    private LocalDate checkIn;
    private LocalDate checkOut;

}
