package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.BookRequest;
import airbnbb11.dto.request.UpdateBookRequest;
import airbnbb11.dto.response.BookingResponse;
import airbnbb11.dto.response.ToBookResponse;
import com.stripe.exception.StripeException;

import java.util.List;

public interface BookingsService {
    List<BookingResponse> getAllBookingsByHouseId(Long houseId);
    ToBookResponse createBooking(BookRequest request);
    Response updateRequestToBook(UpdateBookRequest request) throws StripeException;
    Response deleteBooking(long bookingId);

}
