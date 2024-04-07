package airbnbb11.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
@Builder
public class ToBookResponse {
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDate date;
    private HttpStatus httpStatus;
    private String message;
}
