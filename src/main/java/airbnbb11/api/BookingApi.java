package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.BookRequest;
import airbnbb11.dto.request.UpdateBookRequest;
import airbnbb11.dto.response.BookingResponse;
import airbnbb11.dto.response.ToBookResponse;
import airbnbb11.service.BookingsService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Bookings API", description = "Bookings endpoints")
public class BookingApi {

    private final BookingsService bookingsService;

    @Operation(summary = "Get all bookings",
            description = "Get all bookings by house id/User only")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public List<BookingResponse> getAllBookingsByHouseId(@RequestParam Long houseId) {
        return bookingsService.getAllBookingsByHouseId(houseId);
    }
    @Operation(summary = "Request to book",
            description = "Any registered user can submit a booking request.")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ToBookResponse sendRequestToBook(@Valid @RequestBody BookRequest request) {
        return bookingsService.createBooking(request);
    }

    @Operation(summary = "Delete booking by user and house id", description = "Only Admin has access")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public Response deleteBookingByUserAndHouseId(@RequestParam long bookingId){
        return bookingsService.deleteBooking(bookingId);
    }

    @Operation(summary = "Update booking",
            description = "Update booking by id")
    @PutMapping
    @PreAuthorize("hasAuthority('USER')")
    public Response updateBooking(@RequestBody @Valid UpdateBookRequest request) throws StripeException {
        return bookingsService.updateRequestToBook(request);
    }
}
