package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.response.*;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.PriceType;
import airbnbb11.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User API", description = "User endpoints")
public class UserApi {

    private final UserService userService;
  
    @Operation(summary = "Get User Profile",
            description = "Get User Profile by Token / User Only")
    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/profile")
    public UserProfileResponse getUserProfile() {
        return userService.getUserProfile();
    }

    @Operation(summary = "Get Users on Moderation Houses",
            description = "Get Users on Moderation Houses / User Only")
    @PreAuthorize("permitAll()")
    @GetMapping("/moderation/houses")
    public List<UserHouseResponse> getUsersOnModerationHouses() {
        return userService.getUsersOnModerationHouses();
    }

    @Operation(summary = "Get User Houses",
            description = "Get User Houses by Token / User Only")
    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/houses")
    public List<UserHouseResponse> getUserHouses() {
        return userService.getUserHouses();
    }

    @Operation(summary = "Get User Bookings",
            description = "Get User Bookings by Token / User Only")
    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/bookings")
    public List<UserBookingResponse> getUserBookings() {
        return userService.getUserBookings();

    }

    @Operation(summary = "Get User Profile",
            description = "Get User Profile by Id ")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/user/{userId}")
    public UserProfileResponse getByIdUser(@PathVariable Long userId) {
        return userService.getByIdUser(userId);
    }

    @Operation(summary = "Delete User ",
            description = "Delete User by Id ")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("{userId}")
    public Response deleteUserById(@PathVariable Long userId) {
        return userService.deleteUserById(userId);
    }


    @Operation(summary = "Any registered user can filter announcements in the profile",
            description = "Filter accepted announcements by popular,house type, and price low to high and high to low")
    @GetMapping("/filter")
    public FilterResponseUser getAllAnnouncementsFilters(
            @RequestParam(required = false) HouseType houseType,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) PriceType price) {
        return userService.getAllAnnouncementsFilters(houseType, rating, price);
    }
}
