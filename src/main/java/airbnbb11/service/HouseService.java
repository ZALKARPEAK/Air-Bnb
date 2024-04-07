package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.dto.house.HouseRequest;
import airbnbb11.dto.house.HouseResponse;
import airbnbb11.dto.house.PaginationHouseResponse;
import airbnbb11.dto.response.*;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface HouseService {

    Response saveHouse(HouseRequest houseRequest) throws BadRequestException;
    Response updateHouse(long houseId, HouseRequest houseRequest) throws BadRequestException;

    HouseResponse findHouseById(Long id);

    Response deleteHouseById(Long id);
    List<AllHousesResponse> getAllHouses();

    List<UserHouseResponse> getUserHouses(Long userId);

    List<UserBookingResponse> getUserBookings(Long userId);

    Response blockedHouseById(Long houseId,boolean blockOrUnblock);

    Response blockedAllHouses(Long userId,boolean blockOrUnblock);
  
    PaginationHouseResponse filteredHouses(Region region, HouseType houseType, String rating, String price, int currentPage, int pageSize);

    List<AllHousesResponse> getHousesByFilter(String status, HouseType houseType, String rating, String price);

    PaginationHouseResponse getAllApplications(int currentPage, int pageSize);

    GlobalSearchResponse search(String word, boolean isNearby, Double latitude, Double longitude);
    Response approveApplication(long applicationId, String value, String messageFromAdminToUser);

    LatestAnnouncementResponse getLatestAnnouncement();

    List<PopularHouseResponse> getPopularHouses();

    PopularApartmentResponse getPopularApartment();
}
