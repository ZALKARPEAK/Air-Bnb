package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.response.FavoriteHousesResponse;
import airbnbb11.dto.response.FavoritesResponse;

import java.util.List;

public interface FavoriteService {
    List<FavoritesResponse> getAllFavoritesByHouseId(Long houseId);
    List<FavoriteHousesResponse> getAllFavoriteHouses();

    Response addOrRemoveFavorite(Long houseId);
}
