package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.response.*;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.PriceType;

import java.util.List;

public interface UserService {
    void increaseFailedLoginAttempts(User user);

    void resetFailedLoginAttempts(String username);

    void lockUser(String username);

    User findByAuth();

    boolean unlockUser(String username);

    List<UsersResponse> getAllUsers();

    UserProfileResponse getUserProfile();

    List<UserHouseResponse> getUserHouses();

    List<UserBookingResponse> getUserBookings();

    List<UserHouseResponse> getUsersOnModerationHouses();

    UserProfileResponse getByIdUser(Long userId);

    Response deleteUserById(Long userId);

    FilterResponseUser getAllAnnouncementsFilters(HouseType houseType, String rating, PriceType price);
}
