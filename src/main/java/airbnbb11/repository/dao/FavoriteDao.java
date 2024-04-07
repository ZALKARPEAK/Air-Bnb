package airbnbb11.repository.dao;

import airbnbb11.dto.response.FavoriteHousesResponse;

import java.util.List;

public interface FavoriteDao {
    List<FavoriteHousesResponse> getAllFavoriteHouses();

}
