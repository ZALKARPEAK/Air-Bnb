package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.BookRequest;
import airbnbb11.dto.request.UpdateBookRequest;
import airbnbb11.dto.request.UserResponse;
import airbnbb11.dto.response.BookingResponse;
import airbnbb11.dto.response.ToBookResponse;
import airbnbb11.entities.Booking;
import airbnbb11.entities.House;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.Role;
import airbnbb11.exception.BadRequestException;
import airbnbb11.exception.EntityNotFoundException;
import airbnbb11.exception.ForbiddenException;
import airbnbb11.repository.BookingRepository;
import airbnbb11.repository.HouseRepository;
import airbnbb11.repository.UserRepository;
import airbnbb11.service.BookingsService;
import airbnbb11.service.UserService;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingsServiceImpl implements BookingsService {

    private final BookingRepository bookingRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    @Override
    public List<BookingResponse> getAllBookingsByHouseId(Long houseId) {
        List<Booking> bookings = bookingRepository.getAllByHouseId(houseId);
        return bookings.stream().map(booking -> BookingResponse.builder()
                .id(booking.getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .price(String.valueOf(booking.getHouse().getPrice()))
                .userResponse(new UserResponse(booking.getUser().getId(),
                        booking.getUser().getFirstName() + " " + booking.getUser().getLastName(),
                        booking.getUser().getEmail(), booking.getUser().getImage()))
                .build()).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public ToBookResponse createBooking(BookRequest request) {
        User user = userService.findByAuth();
        House house = houseRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("House with id " + request.getId() + " not found!"));

        log.info("House found: {}", house);
        if (!house.getHouseStatus().equals(HouseStatus.ACCEPTED)) {
            throw new BadRequestException("You cannot book this house!");
        }
        if (request.getId() == 0 || request.getCheckIn() == null || request.getCheckOut() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Invalid or incomplete information!");
        }
        if (request.getCheckIn().isAfter(request.getCheckOut()) || request.getCheckIn().equals(request.getCheckOut())) {
            throw new BadRequestException("Dates are incorrect!");
        }
        List<Booking> existingBookings = bookingRepository.getAllByHouseId(request.getId());

        findTakenDates(request.getCheckIn(), request.getCheckOut(), existingBookings);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHouse(house);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        user.getBookings().add(booking);

        if (request.getAmount() < house.getPrice() * booking.getCheckIn().until(booking.getCheckOut()).getDays()) {
            throw new BadRequestException("Amount is not enough!");
        }else if(request.getAmount() > house.getPrice() * booking.getCheckIn().until(booking.getCheckOut()).getDays()){
            throw new BadRequestException("Amount is too enough");
        }

        Booking save = bookingRepository.save(booking);
        userRepository.save(user);
        log.info("Booking successful for user: {}, house: {}", user.getId(), house.getId());
        String message = "Booking is successful!";

        return ToBookResponse.builder()
                .id(save.getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .httpStatus(HttpStatus.OK)
                .message(message)
                .build();
    }

    @SneakyThrows
    @Override
    public Response updateRequestToBook(UpdateBookRequest request) {

        if (request.getCheckIn().isAfter(request.getCheckOut()) ||
                request.getCheckIn().equals(request.getCheckOut())) {
            throw new BadRequestException("Date is incorrect!");
        }
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new NotFoundException("Booking with id " + request.getBookingId() + " not found!"));
        log.info("Fetching house details for house ID: {}", request.getHouseId());
        House house = houseRepository.findById(request.getHouseId())
                .orElseThrow(() -> new NotFoundException("House with id " + request.getHouseId() + " not found!"));
        User user = userService.findByAuth();
        if (!booking.getUser().getId().equals(user.getId()) || !booking.getHouse().getId().equals(request.getHouseId())) {
            throw new ForbiddenException("incorrect id");
        }

        int prevSum = booking.getCheckIn().until(booking.getCheckOut()).getDays() * booking.getHouse().getPrice();

        List<Booking> existingBookings = bookingRepository.getAllByHouseId(request.getHouseId());

        findTakenDates(request.getCheckIn(), request.getCheckOut(), existingBookings);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());

        int newSum = booking.getCheckIn().until(booking.getCheckOut()).getDays() * booking.getHouse().getPrice();

        if (request.getAmount() < newSum - prevSum) {
            throw new BadRequestException("Amount is not enough!");
        }else if(request.getAmount() > newSum - prevSum){
            throw new BadRequestException("Amount is too enough!");
        }

        log.info("Saving updated booking details");
        bookingRepository.save(booking);
        houseRepository.save(house);
        String message = "The dates has been updated!!";
        log.info("Booking request successfully updated for booking ID: {}", request.getBookingId());

        return Response.builder()
                .message(message)
                .build();
    }

    @Override
    public Response deleteBooking(long bookingId) {
        User admin= userService.findByAuth();
        User user;
        if(admin.getRole().equals(Role.ADMIN)) {
            user = userRepository.getUserByBookingId(bookingId);
            List<Booking> bookings = user.getBookings();
            Optional<Booking> booking = bookings.stream().filter(h -> h.getId() == bookingId).findFirst();
            if(booking.isEmpty()){
                throw new EntityNotFoundException("Booking is not exist");
            }
            booking.ifPresent(bookings::remove);
            user.setBookings(bookings);
            userRepository.save(user);
            booking.ifPresent(bookingRepository::delete);
        }

        return new Response("Booking successfully deleted!");
    }

    public void findTakenDates(LocalDate checkIn, LocalDate checkOut, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.getCheckOut().isAfter(checkIn) && booking.getCheckIn().isBefore(checkOut)) {
                throw new BadRequestException("Intermediate dates of your booking are taken!");
            }
        }
    }
}
