package airbnbb11.repository.dao;

import airbnbb11.dto.response.*;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.PriceType;
import airbnbb11.entities.enums.Region;
import airbnbb11.exception.EntityNotFoundException;
import airbnbb11.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;


    @Override
    public List<UsersResponse> findAll() {
        String sql = """
            SELECT concat(first_name,' ',last_name) as username,
            u.id as user_id,
            u.email,
            (select count(h.id)) as houses
            from users u
            join house h on u.id=h.user_id
            group by u.id order by u.id desc;
            """;
        List<UsersResponse> userResponses = jdbcTemplate.query(sql, ((rs, rowNum) ->
                new UsersResponse(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getInt("houses")
                )));

        String sql1 = """
            SELECT count(b.id) as bookings, u.id as user_id
            from users u
            join booking b on u.id=b.user_id
            group by u.id order by u.id desc;
            """;
        jdbcTemplate.query(sql1, (rs, rowNum) -> {
            long userId = rs.getLong("user_id");
            UsersResponse user = userResponses.stream()
                    .filter(u -> u.getId() == userId)
                    .findFirst()
                    .orElse(null);
            if (user != null) {
                user.setBookingsQuantity(rs.getLong("bookings"));
            }
            return null;
        });

        return userResponses;
    }

    public boolean isBlocked(Long id) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT 1 FROM house WHERE house_status = 'BLOCK' AND id = ?) THEN true ELSE false END as is_blocked";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    @Override
    public List<UserHouseResponse> findUserHouses(Long id) {
        String sql = """
                SELECT
                                  h.id as house_id,
                                  h.price as price,
                                  h.max_guests as max_guests,
                                  h.title as title,
                                  h.description as description,
                                  h.house_status as status,
                                  h.message_from_admin as messages_from_admin,
                                  a.address as address,
                                  a.region as region,
                                  STRING_AGG(DISTINCT hi.image, ', ') as images,
                                  AVG(f.rating) as rating,
                                  COUNT(b.id) as bookings,
                                  COUNT(f2.id) as favorites
                              from house h
                              inner join users u on h.user_id = u.id
                              inner join address a on a.id = h.address_id
                              left join feedback f on h.id = f.house_id
                              left join house_images hi on h.id = hi.house_id
                              left join booking b on h.id = b.house_id
                              left join favourite f2 ON h.id = f2.house_id
                              where
                                  h.user_id = ?
                                  AND h.house_status != 'MODERATING'
                                  AND h.house_status != 'REJECTED'
                              GROUP BY
                                  h.id,
                                  h.price,
                                  h.max_guests,
                                  h.title,
                                  h.description,
                                  h.house_status,
                                  h.message_from_admin,
                                  a.address,
                                  a.region;
                              
                """;
        log.info("User houses  " + id);

        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {

            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();

            return new UserHouseResponse(
                    rs.getLong("house_id"),
                    images,
                    rs.getInt("price"),
                    rs.getDouble("rating"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("address"),
                    rs.getInt("max_guests"),
                    rs.getString("status"),
                    rs.getInt("bookings"),
                    rs.getString("messages_from_admin"),
                    rs.getString("region"),
                    isBlocked(rs.getLong("house_id")),
                    rs.getInt("favorites"));
        });
    }

    @Override
    public List<UserBookingResponse> findUserBookings(Long id) {
        String sql = """
                           
                    select
                                   b.id as booking_id,
                                   STRING_AGG(distinct hi.image, ', ') as images,
                                   h.price as price,
                                   AVG(f.rating) as rating,
                                   h.title as title,
                                   h.description as description,
                                   a.address as address,
                                   h.max_guests as max_guests,
                                   a.region as region,
                                   b.check_in as check_in,
                                   b.check_out as check_out
                               from
                                   booking b
                                   inner join house h on b.house_id = h.id
                                   inner join address a on a.id = h.address_id
                                   left join house_images hi on h.id = hi.house_id
                                   left join feedback f on h.id = f.house_id
                               where
                                   b.user_id = ?
                               group by 
                                   b.id, a.id, h.price, h.title, h.description, a.address, h.max_guests, a.region, b.check_in, b.check_out;
                               
                """;
        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();

            log.info("Executing SQL query to find user bookings for user with id: {}", id);
            return new UserBookingResponse(
                    rs.getLong("booking_id"),
                    images,
                    rs.getInt("price"),
                    rs.getDouble("rating"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("address"),
                    rs.getInt("max_guests"),
                    rs.getString("region"),
                    rs.getString("check_in"),
                    rs.getString("check_out"));
        });
    }

    @Override
    public List<UserHouseResponse> findUserModerationHouses(Long id) {
        String sql = """
                select
                                  h.id as house_id,
                                  h.price as price,
                                  h.max_guests as max_guests,
                                  h.title as title,
                                  h.description as description,
                                  h.house_status as status,
                                  h.message_from_admin as messages_from_admin,
                                  a.address as address,
                                  a.region as region,
                                  STRING_AGG(distinct hi.image, ', ') as images,
                                  avg(f.rating) as rating,
                                  count(b.id) as bookings,
                                  count(f2.id) as favorites
                              from
                                  house h
                                  inner join users u on h.user_id = u.id
                                  inner join address a on a.id = h.address_id
                                  left join feedback f on h.id = f.house_id
                                  left join house_images hi on h.id = hi.house_id
                                  left join booking b on h.id = b.house_id
                                  left join favourite f2 on h.id = f2.house_id
                              where
                                  h.user_id = ? and h.house_status = 'MODERATING'
                              group by 
                                  h.id,
                                  h.price,
                                  h.max_guests,
                                  h.title,
                                  h.description,
                                  h.house_status,
                                  h.message_from_admin,
                                  a.address,
                                  a.region;
                              
                """;
        log.info("User moderation houses  " + id);
        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {

            String imagesConcatenated = rs.getString("images");

            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();

            return new UserHouseResponse(
                    rs.getLong("house_id"),
                    images,
                    rs.getInt("price"),
                    rs.getDouble("rating"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("address"),
                    rs.getInt("max_guests"),
                    rs.getString("status"),
                    rs.getInt("bookings"),
                    rs.getString("messages_from_admin"),
                    rs.getString("region"),
                    isBlocked(rs.getLong("house_id")),
                    rs.getInt("favorites"));
        });
    }

    @Override
    public UserProfileResponse getByIdUser(Long userId) {
        String sql = "SELECT u.image, concat(first_name,' ',last_name) as name, u.email FROM users u WHERE u.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (resultSet, rowNum) -> {
            UserProfileResponse userProfile = new UserProfileResponse();
            userProfile.setImage(resultSet.getString("image"));
            userProfile.setName(resultSet.getString("name"));
            userProfile.setEmail(resultSet.getString("email"));
            return userProfile;
        });
    }


    public User findByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findUserByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + authentication.getName() + " not found!")
        );
    }

    @Override
    public FilterResponseUser getAllAnnouncementsFilters(HouseType houseType, String rating, PriceType price) {
        User user = findByAuth();
        StringBuilder sql = new StringBuilder("SELECT a.id, a.price, a.max_guests, addr.address, a.description, addr.province, addr.region, a.house_type, a.house_status, a.title, COALESCE(r.rating, 0) as rating, ");
        sql.append("(SELECT ARRAY_AGG(ai.image) FROM house_images ai WHERE ai.house_id = a.id) as image ");
        sql.append("FROM house a ");
        sql.append("LEFT JOIN feedback r ON a.id = r.house_id ");
        sql.append("LEFT JOIN address addr ON a.address_id = addr.id ");
        sql.append("WHERE a.user_id = ? AND a.house_status = 'ACCEPTED' ");

        List<Object> params = new ArrayList<>();
        params.add(user.getId());

        if (houseType != null) {
            sql.append("AND a.house_type = ? ");
            params.add(houseType.name());
        }

        if (rating != null && !rating.isEmpty()) {
            sql.append("AND r.rating IS NOT NULL ");
        }

        if (price != null) {
            sql.append("AND a.price IS NOT NULL ");
        }

        sql.append("GROUP BY a.id, a.price, a.max_guests, addr.address, a.description, addr.province, a.house_type, a.house_status, addr.region, a.title, rating, image ");

        if (rating != null && !rating.isEmpty()) {
            sql.append("ORDER BY rating " + (rating.equalsIgnoreCase("asc") ? "ASC" : "DESC"));
        } else if (price != null) {
            sql.append("ORDER BY a.price " + (price.equals(PriceType.LOW_TO_HIGH) ? "ASC" : "DESC"));
        }

        log.info("Fetching announcements with filters: HouseType - " + houseType + ", Rating - " + rating + ", PriceType - " + price);

        List<FilterResponse> filterResponses = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            List<String> images = new ArrayList<>();
            Array imageArray = rs.getArray("image");
            if (imageArray != null) {
                images = Arrays.asList((String[]) imageArray.getArray());
            }
            return FilterResponse.builder()
                    .id(rs.getLong("id"))
                    .price(rs.getInt("price"))
                    .maxGuests(rs.getInt("max_guests"))
                    .houseType(HouseType.valueOf(rs.getString("house_type")))
                    .address(rs.getString("address"))
                    .description(rs.getString("description"))
                    .province(rs.getString("province"))
                    .title(rs.getString("title"))
                    .rating(rs.getInt("rating"))
                    .houseStatus(HouseStatus.valueOf(rs.getString("house_status")))
                    .region(Region.valueOf(rs.getString("region")))
                    .images(images)
                    .build();
        }, params.toArray());

        FilterResponseUser filterResponse = new FilterResponseUser(filterResponses);


        log.info("Fetched announcements with filters successfully!");
        return filterResponse;

    }
}
