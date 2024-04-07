package airbnbb11.repository.dao;

import airbnbb11.dto.response.FavoriteHousesResponse;
import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.service.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FavoriteDaoImpl implements FavoriteDao {

    private final UserServiceImpl userService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<FavoriteHousesResponse> getAllFavoriteHouses() {
        List<FavoriteHousesResponse> favorites = new ArrayList<>();
        if (userService.findByAuth().getFavorites().isEmpty()) {
            return favorites;
        }
        String sql = """
               select a.id,
                                             STRING_AGG(DISTINCT hi.image, ', ') as images,
                                             a.price,
                                             (select sum(f.rating) / count(f) from feedback f where f.house_id = a.id) as rating,
                                             a.description,
                                             ad.address,
                                             a.max_guests,
                                             a.house_status,
                                             case when f.house_id is not null then true else false end as is_favorite
                                      from house a
                                      JOIN address ad ON a.address_id = ad.id
                                      LEFT JOIN house_images hi ON a.id = hi.house_id
                                      JOIN favourite f ON f.house_id = a.id
                                      WHERE f.user_id = ?
                                      GROUP BY a.id, a.price, a.description, ad.address, a.max_guests, a.house_status, f.house_id;
                """;
        log.info("Fetching all favorite announcements.");

        List<FavoriteHousesResponse> favoriteAnnouncements = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();
            return FavoriteHousesResponse.builder()
                    .id(rs.getLong("id"))
                    .images(images)
                    .price(rs.getInt("price"))
                    .address(rs.getString("address"))
                    .description(rs.getString("description"))
                    .houseStatus(HouseStatus.valueOf(rs.getString("house_status")))
                    .maxGuests(rs.getInt("max_guests"))
                    .rating(rs.getDouble("rating"))
                    .isFavorite(rs.getBoolean("is_favorite"))
                    .build();
        }, userService.findByAuth().getId());


        log.info("Fetched all favorite announcements successfully!");
        return favoriteAnnouncements;
    }
}
