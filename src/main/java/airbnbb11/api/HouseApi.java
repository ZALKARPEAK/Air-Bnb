package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.house.HouseRequest;
import airbnbb11.dto.house.HouseResponse;
import airbnbb11.dto.house.PaginationHouseResponse;

import airbnbb11.dto.response.GlobalSearchResponse;

import airbnbb11.dto.response.LatestAnnouncementResponse;
import airbnbb11.dto.response.PopularApartmentResponse;
import airbnbb11.dto.response.PopularHouseResponse;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import airbnbb11.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/houses")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "House API", description = "House endpoints")
public class HouseApi {

    private final HouseService houseService;

    @Operation(summary = "Save house",
            description = "Save house / User Only")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping
    public Response saveHouse(@RequestBody HouseRequest houseRequest) throws BadRequestException {
        return houseService.saveHouse(houseRequest);
    }

    @Operation(summary = "Update house", description = "Update house / User Only")
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping("/update")
    public Response updateHouse(@RequestParam long houseId, @RequestBody HouseRequest houseRequest) throws BadRequestException {
        return houseService.updateHouse(houseId, houseRequest);
    }

    @Operation(summary = "Get house",
            description = "Get house by id / User only ")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/{houseId}")
    public HouseResponse findHouseById(@PathVariable Long houseId) {

        return houseService.findHouseById(houseId);
    }

    @Operation(summary = "Delete house",
            description = "House can be deleted by id by admin or user who created it.")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @DeleteMapping("/{houseId}")
    public Response deleteHouseById(@PathVariable Long houseId) {

        return houseService.deleteHouseById(houseId);
    }

    @Operation(summary = "Filtered houses",
            description = "Filter houses by region, house type, rating and price/User only")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/filtered")
    public PaginationHouseResponse filteredHouses(@RequestParam(required = false) Region region,
                                                  @RequestParam(required = false) HouseType houseType,
                                                  @RequestParam(required = false) String rating,
                                                  @RequestParam(required = false) String price,
                                                  @RequestParam int currentPage,
                                                  @RequestParam int pageSize) {

        return houseService.filteredHouses(region, houseType, rating, price, currentPage, pageSize);
    }

    @Operation(summary = "Blocking and unblocking  house",
            description = "Only admin can block  and unblock house ")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/blockedHousesById")
    public Response blockedHouseById(Long houseId, boolean blockOrUnblock) {
        return houseService.blockedHouseById(houseId, blockOrUnblock);
    }

    @Operation(summary = "Blocking all houses",
            description = "Only admin can block  user's houses")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/blockAllAds/{userId}")
    public Response blockedAllHousesByUserId(@PathVariable Long userId, boolean blockOrUnblock) {
        return houseService.blockedAllHouses(userId, blockOrUnblock);
    }

    @PermitAll
    @Operation(summary = "Get latest announcement",
            description = "Get latest announcement by create date")
    @GetMapping("/latestAnnouncement")
    public LatestAnnouncementResponse getLatestAnnouncement() {
        return houseService.getLatestAnnouncement();
    }

    @PermitAll
    @Operation(summary = "Get popular houses",
            description = "Get popular houses by rating")
    @GetMapping("/getPopularHouses")
    public List<PopularHouseResponse> getPopularHouses() {
        return houseService.getPopularHouses();
    }

    @PermitAll
    @Operation(summary = "Get popular apartment ",
            description = "Get popular apartment by rating")
    @GetMapping("/getPopularApartment")
    public PopularApartmentResponse getPopularApartment() {
        return houseService.getPopularApartment();
    }

    @Operation(summary = "Global search",
            description = "You can search announcements by region,city,house and apartment")
    @GetMapping("/global-search")
    public GlobalSearchResponse search(@RequestParam String word, @RequestParam(required = false) boolean isNearby,
                                       @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) {
        return houseService.search(word, isNearby, latitude, longitude);
    }

}