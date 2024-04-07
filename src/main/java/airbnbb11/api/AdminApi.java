package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.dto.house.PaginationHouseResponse;
import airbnbb11.dto.response.UserBookingResponse;
import airbnbb11.dto.response.UserHouseResponse;
import airbnbb11.dto.response.UsersResponse;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.service.HouseService;
import airbnbb11.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Admin api", description = "API for admin management")
public class AdminApi {

    private final HouseService houseService;
    private final UserService userService;

    @Operation(summary = "Get all applications by pagination",
            description = "Get all applications by status 'MODERATING'")
    @GetMapping("/applications")
    public PaginationHouseResponse getAllAnnouncementsModerationAndPagination(@RequestParam int currentPage,
                                                                              @RequestParam int pageSize) {
        return houseService.getAllApplications(currentPage, pageSize);
    }

    @Operation(summary = "Filter and sort houses",
            description = "Filter houses by status: " +
                    "TRUE || FALSE || NULL ,house type," +
                    "rating : ASC || DESC and price : LOW TO HIGH || HIGH TO LOW" +
                    "/Admin only")
    @GetMapping("/house-filter")
    public List<AllHousesResponse> getHousesFilterAndSort(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) HouseType houseType,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String price) {

        return houseService.getHousesByFilter(status, houseType, rating, price);
    }


    @Operation(summary = "Get user bookings",
            description = "Admin get bookings by user id")
    @GetMapping("/bookings/{userId}")
    public List<UserBookingResponse> getUserBookings(@PathVariable Long userId){

        return houseService.getUserBookings(userId);
    }

    @Operation(summary = "Get user announcements",
            description = "Admin get announcements by user id")
    @GetMapping("/announcements/{userId}")
    public List<UserHouseResponse> getUserHouses(@PathVariable Long userId){

        System.out.println("api");
        return houseService.getUserHouses(userId);
    }

    @Operation(summary = "Get All Houses",
            description = "Get all Houses / Admin Only")
    @GetMapping("/houses")
    public List<AllHousesResponse> getAllHouses() {

        return houseService.getAllHouses();
    }
    @Operation(summary = "Get All Users",
            description = "Get all Users / Admin Only")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public List<UsersResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "APPROVE, BLOCK, REJECT", description = "Admin APPROVE, BLOCK, REJECT application")
    @PostMapping("/accepted-application/{applicationId}")
    public Response approveApplication(@PathVariable long applicationId, @RequestParam String value, @RequestParam(required = false) String messageFromAdminToUser){
        return houseService.approveApplication(applicationId, value, messageFromAdminToUser);
    }
}
