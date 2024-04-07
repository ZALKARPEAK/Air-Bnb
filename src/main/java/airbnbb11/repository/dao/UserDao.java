package airbnbb11.repository.dao;

import airbnbb11.dto.response.*;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.PriceType;


import java.util.List;

public interface UserDao {
    List<UsersResponse> findAll();

    List<UserHouseResponse> findUserHouses(Long id);

    List<UserBookingResponse> findUserBookings(Long id);

    List<UserHouseResponse> findUserModerationHouses(Long id);
    UserProfileResponse getByIdUser(Long userId);

    FilterResponseUser getAllAnnouncementsFilters(HouseType houseType, String rating, PriceType price);

}
