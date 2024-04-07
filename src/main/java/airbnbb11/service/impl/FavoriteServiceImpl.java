package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.UserResponse;
import airbnbb11.dto.response.FavoriteHousesResponse;
import airbnbb11.dto.response.FavoritesResponse;
import airbnbb11.entities.Favourite;
import airbnbb11.entities.House;
import airbnbb11.entities.User;
import airbnbb11.repository.FavoriteRepository;
import airbnbb11.repository.HouseRepository;
import airbnbb11.repository.dao.FavoriteDao;
import airbnbb11.service.FavoriteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    private final HouseRepository houseRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteDao favoriteDao;
    private final UserServiceImpl userService;

    @Override
    public List<FavoritesResponse> getAllFavoritesByHouseId(Long houseId) {
        List<Favourite> favorites = favoriteRepository.getAllByHouseId(houseId);
        return favorites.stream().map(favorite -> FavoritesResponse.builder()
                .id(favorite.getId())
                .createdAt(favorite.getCreatedDate())
                .userResponse(new UserResponse(favorite.getUser().getId(),
                        favorite.getUser().getFirstName() + " " + favorite.getUser().getLastName(),
                        favorite.getUser().getEmail(),
                        favorite.getUser().getImage()))
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<FavoriteHousesResponse> getAllFavoriteHouses() {
        return favoriteDao.getAllFavoriteHouses();
    }

    @Override
    public Response addOrRemoveFavorite(Long houseId) {
        User user = userService.findByAuth();

        House house = houseRepository.findById(houseId).orElseThrow(()
                -> new EntityNotFoundException("House Not found"));


        boolean isFavorite = false;
        for (Favourite f : user.getFavorites()) {
            if (Objects.equals(f.getHouse().getId(), houseId)) {
                favoriteRepository.delete(f);
                isFavorite = true;
                log.info("House with id: {} was deleted from favorites for user: {}", houseId, user.getEmail());
                break;
            }
        }

        if (!isFavorite) {
            Favourite favourite = new Favourite();
            favourite.setHouse(house);
            favourite.setUser(user);
            favoriteRepository.save(favourite);
            log.info("House with id: {} was added to favorites for user: {}", houseId, user.getEmail());
        }

        if (isFavorite) {
            return Response.builder()
                    .message(String.format("House with id: %d was deleted from your favorites.", houseId))
                    .build();
        } else {
            return Response.builder()
                    .message(String.format("House with id: %d was added to your favorites!", houseId))
                    .build();
        }
    }
}
