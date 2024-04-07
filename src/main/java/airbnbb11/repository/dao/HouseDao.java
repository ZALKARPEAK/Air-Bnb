package airbnbb11.repository.dao;

import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.dto.house.PaginationHouseResponse;

import airbnbb11.dto.response.GlobalSearchResponse;

import airbnbb11.dto.response.LatestAnnouncementResponse;
import airbnbb11.dto.response.PopularApartmentResponse;
import airbnbb11.dto.response.PopularHouseResponse;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;

import java.util.List;

public interface HouseDao {
    PaginationHouseResponse getAllHousesFilter(long userId, Region region, HouseType houseType, String rating, String price, int currentPage, int pageSize);
    List<AllHousesResponse> getHousesByFilter(String status, HouseType houseType, String rating, String price);

    PaginationHouseResponse getAllApplications(int currentPage, int pageSize);
    GlobalSearchResponse search(String word, boolean isNearby, Double latitude, Double longitude);

    LatestAnnouncementResponse getLatestAnnouncement();

    List<PopularHouseResponse> getPopularHouses();

    PopularApartmentResponse getPopularApartment();

}
