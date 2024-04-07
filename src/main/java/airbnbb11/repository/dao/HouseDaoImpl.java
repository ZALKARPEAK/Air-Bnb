package airbnbb11.repository.dao;

import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.dto.house.PagHouseResponse;
import airbnbb11.dto.house.PaginationHouseResponse;

import airbnbb11.dto.response.GlobalSearchResponse;
import airbnbb11.dto.response.HouseSearchResponse;

import airbnbb11.dto.response.LatestAnnouncementResponse;
import airbnbb11.dto.response.PopularApartmentResponse;
import airbnbb11.dto.response.PopularHouseResponse;

import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import airbnbb11.exception.BadCredentialsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class HouseDaoImpl implements HouseDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public PaginationHouseResponse getAllHousesFilter(long userId, Region region, HouseType houseType, String rating, String price, int currentPage, int pageSize) {
        String sql = """
                              SELECT a.id,
                                                                        a.price,
                                                                        a.max_guests,
                                                                        ad.address,
                                                                        a.description,
                                                                        ad.province,
                                                                        ad.region,
                                                                        a.title,
                                                                        a.house_type,
                                                                        r.rating,
                                                                        STRING_AGG(DISTINCT hi.image, ', ') as images,
                                                                        CASE WHEN f.house_id IS NOT NULL THEN true ELSE false END as is_favorite,
                                                                        CASE WHEN b.house_id IS NOT NULL THEN true ELSE false END as booked
                                                                 FROM house a
                                                                 INNER JOIN address ad on a.address_id = ad.id
                                                                  JOIN feedback r ON a.id = r.house_id
                                                                 LEFT JOIN house_images hi on a.id = hi.house_id
                                                                 LEFT JOIN favourite f ON a.id = f.house_id AND f.user_id = ?
                                                                 LEFT JOIN (
                                                                     SELECT DISTINCT house_id
                                                                     FROM booking
                                                                     WHERE user_id = ?
                                                                 ) b ON a.id = b.house_id
                                                                 WHERE a.house_status = 'ACCEPTED'
                                                                 
                """;
        log.info("Filtering houses with parameters: region = " + region + ", houseType = " + houseType + ", rating = " + rating + ", price = " + price + ", currentPage = " + currentPage + ", pageSize = " + pageSize);

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);
        if (region != null) {
            sql += "AND ad.region = ? ";
            params.add(region.name());
        }

        if (houseType != null) {
            sql += "AND a.house_type = ? ";
            params.add(houseType.name());
        }
        if (price != null && !price.isEmpty()) {
            sql += "AND a.price IS NOT NULL ";
        }

        sql += "GROUP BY a.id, a.price, a.max_guests, ad.address, a.description, ad.province, ad.region, a.title, r.rating,a.house_type,is_favorite,booked ";

        if ( price == null && rating != null && !rating.isEmpty()) {
            sql += "ORDER BY r.rating " + (rating.equalsIgnoreCase("DESC") ? "DESC" : "ASC");
        }
        if (price != null && !price.isEmpty() && rating == null) {
            sql += "ORDER BY a.price " + (price.equalsIgnoreCase("Low to high") ? "ASC" : "DESC");
        }
        if(price != null && !price.isEmpty() && rating != null && !rating.isEmpty()){
            sql += "ORDER BY r.rating " + (rating.equalsIgnoreCase("DESC") ? "DESC" : "ASC")+", a.price " + (price.equalsIgnoreCase("HIGH TO LOW") ? "DESC" : "ASC");
        }
    
        int offset = (currentPage - 1) * pageSize;
        sql += " LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add(offset);
        int totalRecords = getTotalRecordsOfAll();
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        log.info("Filtering houses with SQL: " + sql);
        List<PagHouseResponse> results = jdbcTemplate.query(sql, (rs, rowNum) -> {

            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();

            return new PagHouseResponse(
                    rs.getLong("id"),
                    HouseType.valueOf(rs.getString("house_type")),
                    images,
                    rs.getInt("price"),
                    Region.valueOf(rs.getString("region")),
                    rs.getString("address"),
                    rs.getString("description"),
                    rs.getString("title"),
                    rs.getInt("max_guests"),
                    rs.getString("province"),
                    rs.getInt("rating"),
                    rs.getBoolean("is_favorite"),
                    rs.getBoolean("booked")
            );
        }, params.toArray());
        log.info("Houses filtered successfully!");
        return new PaginationHouseResponse(results, currentPage, pageSize, totalPages);
    }


    @Override
    public List<AllHousesResponse> getHousesByFilter(String status, HouseType houseType, String rating, String price) {
        String sql = """
            SELECT
                a.id,
                a.price,
                a.max_guests,
                ad.address,
                a.description,
                a.house_type,
                ad.province,
                ad.region,
                a.title,
                AVG(r.rating) AS rating,
                STRING_AGG(DISTINCT hi.image, ', ') AS images
            FROM
                house a
            LEFT JOIN
                feedback r ON a.id = r.house_id
            LEFT JOIN
                house_images hi ON hi.house_id = a.id
            JOIN
                address ad ON ad.id = a.address_id
            LEFT JOIN
                booking b ON b.house_id = a.id
            WHERE
                (a.house_status = 'ACCEPTED' OR a.house_status = 'BLOCKED')
            """;

        List<Object> params = new ArrayList<>();

        if (houseType != null) {
            sql += "AND a.house_type = ? ";
            params.add(houseType.name());
        }

        if (status != null) {
            sql += "AND ";
            if (status.equals("true")) {
                sql += "b.id IS NOT NULL ";
            } else if (status.equals("false")) {
                sql += "b.id IS NULL ";
            }
        }

        sql += "GROUP BY a.id, a.price, a.max_guests, ad.address, a.description, a.house_type, ad.province, ad.region, a.title ";

        boolean orderByAdded = false;

        if (rating != null && !rating.isEmpty()) {
            sql += "ORDER BY rating ";
            if (rating.equalsIgnoreCase("asc")) {
                sql += "ASC";
            } else {
                sql += "DESC";
            }
            orderByAdded = true;
        }

        if (price != null) {
            if (orderByAdded) {

                sql += ", ";
            } else {

                sql += "ORDER BY ";
            }
            sql += "a.price ";
            if (price.equalsIgnoreCase("LOW TO HIGH")) {
                sql += "ASC";
            } else if (price.equalsIgnoreCase("HIGH TO LOW")) {
                sql += "DESC";
            }
            orderByAdded = true;
        }

        if (orderByAdded) {
            sql += ";";
        } else {
            sql += ";";
        }

        log.info("Houses filtered successfully!");

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();

            return AllHousesResponse.builder()
                    .id(rs.getLong("id"))
                    .houseType(HouseType.valueOf(rs.getString("house_type")))
                    .images(images)
                    .price(BigDecimal.valueOf(rs.getInt("price")))
                    .region(rs.getString("region"))
                    .address(rs.getString("address"))
                    .description(rs.getString("description"))
                    .title(rs.getString("title"))
                    .maxGuests(rs.getInt("max_guests"))
                    .province(rs.getString("province"))
                    .rating(rs.getInt("rating"))
                    .status(status)
                    .build();
        });
    }




    @Override
    public PaginationHouseResponse getAllApplications(int currentPage, int pageSize) {
        String sql = """
                SELECT a.id          AS id,
                       a.price       AS price,
                       STRING_AGG(DISTINCT hi.image, ', ') as images,
                       a.max_guests  AS max_guests,
                       ad.address     AS address,
                       a.description AS description,
                       ad.province    AS province,
                       a.title       AS title,
                       a.house_status    AS status,
                       AVG(r.rating) AS rating
                FROM house a
                         LEFT JOIN feedback r ON a.id = r.house_id
                         JOIN address ad ON a.address_id = ad.id
                            LEFT JOIN house_images hi ON a.id = hi.house_id
                WHERE a.house_status = 'MODERATING'
                GROUP BY a.id, a.price, a.max_guests, ad.address,
                         a.description, ad.province, a.title, a.created_date
                ORDER BY a.created_date
                                 """;
        int offset = (currentPage - 1) * pageSize;
        sql += "LIMIT ? OFFSET ?";

        int totalRecords = getTotalRecords();
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);


        log.info("Fetching moderated applications with pagination.");

        List<PagHouseResponse> responses = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();
            return PagHouseResponse.builder()
                    .id(rs.getLong("id"))
                    .images(images)
                    .price(rs.getInt("price"))
                    .address(rs.getString("address"))
                    .description(rs.getString("description"))
                    .title(rs.getString("title"))
                    .maxGuests(rs.getInt("max_guests"))
                    .province(rs.getString("province"))
                    .rating(rs.getInt("rating"))
                    .build();
        }, pageSize, offset);
        log.info("Fetched moderated applications with pagination successfully!");
        return new PaginationHouseResponse(responses, currentPage, pageSize, totalPages);

    }

    private int getTotalRecords() {
        String countQuery = "SELECT COUNT(*) FROM house WHERE house_status = 'MODERATING'";
        return jdbcTemplate.queryForObject(countQuery, Integer.class);
    }

    private int getTotalRecordsOfAll() {
        String countQuery = "SELECT COUNT(*) FROM house WHERE house_status = 'ACCEPTED'";
        return jdbcTemplate.queryForObject(countQuery, Integer.class);
    }


    @Override
    public LatestAnnouncementResponse getLatestAnnouncement() {
        String sql = """
        SELECT h.id                              AS id,
                      ad.address                        AS address,
                      h.description                     AS description,
                      h.title                           AS title,
                      STRING_AGG(DISTINCT i.image, ',') as images
               FROM house h
                        JOIN address ad ON h.address_id = ad.id
                        JOIN house_images i ON h.id = i.house_id
               WHERE h.house_status = 'ACCEPTED'
               GROUP BY h.id, ad.address, h.description, h.title, h.created_date, h.created_time
               ORDER BY h.created_time DESC, h.created_date DESC
               LIMIT 1;
         """;
        log.info("Fetching the latest announcement.");

        LatestAnnouncementResponse latestAnnouncement = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            String imagesConcatenated = rs.getString("images");
            List<String> images = imagesConcatenated != null ? Arrays.asList(imagesConcatenated.split(", ")) : new ArrayList<>();
            return LatestAnnouncementResponse.builder()
                    .id(rs.getLong("id"))
                    .images(images)
                    .title(rs.getString("title"))
                    .address(rs.getString("address"))
                    .description(rs.getString("description"))
                    .build();
        });

        log.info("Fetched the latest announcement successfully!");
        return latestAnnouncement;
    }

    @Override
    public List<PopularHouseResponse> getPopularHouses() {
        String sql = """
                SELECT h.id,
                                              ad.address,
                                              h.description,
                                              h.title,
                                              h.price,
                                              f.rating,
                                              coalesce((SELECT string_agg(ai.image, ',')
                                                        FROM house_images ai
                                                        WHERE ai.house_id = h.id), 'null') AS images
                                       FROM house h JOIN address ad ON h.address_id = ad.id
                                                 JOIN feedback f
                                                          ON h.id = f.house_id
                                       WHERE h.house_type = 'HOUSE'
                                         AND h.house_status = 'ACCEPTED'
                                       GROUP BY h.id,
                                                ad.address,
                                                h.description,
                                                h.title,
                                                h.price,
                                                f.rating
                                       ORDER BY f.rating DESC
                                       LIMIT 3
                 """;
        log.info("Fetching popular houses.");

        List<PopularHouseResponse> popularHouses = jdbcTemplate.query(sql, (rs, rowNum) -> PopularHouseResponse.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .address(rs.getString("address"))
                .images(Arrays.asList(rs.getString("images").split(",")))
                .price(rs.getInt("price"))
                .rating(rs.getDouble("rating"))
                .build());

        log.info("Fetched popular houses successfully!");
        return popularHouses;
    }

    @Override
    public PopularApartmentResponse getPopularApartment() {
        String sql = """
                SELECT h.id,
                       ad.address,
                       h.description,
                       h.title,
                       f.rating,
                       array_agg(hi.image) AS images
                FROM house h
                        JOIN feedback f ON h.id = f.house_id
                         JOIN address ad ON h.address_id = ad.id
                            LEFT JOIN house_images hi ON h.id = hi.house_id
                WHERE h.house_type = 'APARTMENT'
                AND h.house_status ='ACCEPTED'
                GROUP BY
                    h.id,
                    ad.address,
                    h.description,
                    h.title,
                    f.rating
                ORDER BY f.rating desc
                LIMIT 1
                    """;
        log.info("Fetching the most popular apartment.");
        PopularApartmentResponse popularApartment = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> PopularApartmentResponse.builder()
                .id(rs.getLong("id"))
                .images(Arrays.asList((String[]) rs.getArray("images").getArray()))
                .title(rs.getString("title"))
                .address(rs.getString("address"))
                .description(rs.getString("description"))
                .build());
        log.info("Fetched the most popular apartment successfully!");
        return popularApartment;
    }


    @Override
    public GlobalSearchResponse search(String word, boolean isNearby, Double latitude, Double longitude) {
        if (isNearby) {
            if (latitude != null && longitude != null) {
                double earthRadius = 6371;
                double distance = 5;
                double latRange = Math.toDegrees(distance / earthRadius);
                double longRange = Math.toDegrees(distance / (earthRadius * Math.cos(Math.toRadians(latitude))));
                double minLat = latitude - latRange;
                double maxLat = latitude + latRange;
                double minLong = longitude - longRange;
                double maxLong = longitude + longRange;
                String query = """
                        SELECT h.id as id,
                               h.house_type as houseType,
                               h.price as price,
                               h.max_guests as max_guests,
                               a.address as address,
                               h.description as description,
                               h.title as title,
                               a.province as province,
                               a.region as region,
                               AVG(r.rating) as rating
                        FROM house h
                         LEFT JOIN feedback r ON h.id = r.house_id
                         JOIN address a ON h.address_id = a.id
                            LEFT JOIN house_images hi ON h.id = hi.house_id
                        WHERE h.house_status = 'ACCEPTED'  AND
                        (a.region ILIKE lower(concat('%', ?, '%'))
                               OR h.house_status ILIKE lower(concat('%', ?, '%'))
                               OR h.house_type ILIKE lower(concat('%', ?, '%'))
                               OR a.province ILIKE lower(concat('%', ?, '%')))
                          AND a.latitude BETWEEN ? AND ?
                          AND a.longitude BETWEEN ? AND ?
                        GROUP BY h.id,h.house_type, h.price, h.max_guests, a.address, h.description, a.province, a.region, h.title
                        """;
                List<HouseSearchResponse> houseSearchResponses = jdbcTemplate.query(query, (rs, rowNum) -> HouseSearchResponse.builder()
                        .id(rs.getLong("id"))
                        .price(rs.getInt("price"))
                        .address(rs.getString("address"))
                        .description(rs.getString("description"))
                        .title(rs.getString("title"))
                        .maxGuests(rs.getInt("max_guests"))
                        .province(rs.getString("province"))
                        .rating(rs.getInt("rating"))
                        .build(), word, word, word, word, minLat, maxLat, minLong, maxLong);
                log.info("Global search successfully!");
                return new GlobalSearchResponse(houseSearchResponses);
            } else {
                throw new BadCredentialsException("Latitude and longitude are null");
            }
        }
        String query = """
                SELECT h.id as id,
                       h.house_type as houseType,
                       h.price as price,
                       h.max_guests as max_guests,
                       a.address as address,
                       h.description as description,
                       h.title as title,
                       a.province as province,
                       a.region as region,
                       AVG(r.rating) as rating
                FROM house h
                 LEFT JOIN feedback r ON h.id = r.house_id
                 JOIN address a ON h.address_id = a.id
                    LEFT JOIN house_images hi ON h.id = hi.house_id
                WHERE h.house_status = 'ACCEPTED'  AND
                (a.region ILIKE lower(concat('%', ?, '%'))
                       OR h.house_status ILIKE lower(concat('%', ?, '%'))
                       OR h.house_type ILIKE lower(concat('%', ?, '%'))
                       OR a.province ILIKE lower(concat('%', ?, '%'))
                       or h.title ILIKE  lower(concat('%',?,'%')))
                       
         
                GROUP BY h.id,h.house_type, h.price, h.max_guests, a.address, h.description, a.province, a.region, h.title
                """;
        List<HouseSearchResponse> results = jdbcTemplate.query(query, (rs, rowNum) -> HouseSearchResponse
                .builder()
                .id(rs.getLong("id"))
                .price(rs.getInt("price"))
                .address(rs.getString("address"))
                .description(rs.getString("description"))
                .title(rs.getString("title"))
                .maxGuests(rs.getInt("max_guests"))
                .province(rs.getString("province"))
                .rating(rs.getInt("rating"))
                .build(), word, word, word, word,word);
        return new GlobalSearchResponse(results);
    }

}


