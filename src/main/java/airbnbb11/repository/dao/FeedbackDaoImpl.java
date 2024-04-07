package airbnbb11.repository.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackDaoImpl implements FeedbackDao{

    private final JdbcTemplate jdbcTemplate;
    @Override
    public Map<String, Object> calculateRatingStats(Long houseId) {
        String sql = "SELECT COUNT(*) as total_feedback, AVG(rating) as overall_rating, "
                + "SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END) as rating_5_count, "
                + "SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END) as rating_4_count, "
                + "SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END) as rating_3_count, "
                + "SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END) as rating_2_count, "
                + "SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END) as rating_1_count "
                + "FROM feedback WHERE house_id = ?";

        log.info("Calculating rating statistics for house with ID: {}", houseId);
        return jdbcTemplate.queryForObject(sql, new Object[]{houseId}, (rs, rowNum) -> {
            Map<String, Object> ratingStats = new HashMap<>();
            int totalFeedback = rs.getInt("total_feedback");
            ratingStats.put("total_feedback", totalFeedback);

            ratingStats.put("rating_5_percentage", (double) rs.getInt("rating_5_count") / totalFeedback * 100);
            ratingStats.put("rating_4_percentage", (double) rs.getInt("rating_4_count") / totalFeedback * 100);
            ratingStats.put("rating_3_percentage", (double) rs.getInt("rating_3_count") / totalFeedback * 100);
            ratingStats.put("rating_2_percentage", (double) rs.getInt("rating_2_count") / totalFeedback * 100);
            ratingStats.put("rating_1_percentage", (double) rs.getInt("rating_1_count") / totalFeedback * 100);

            return ratingStats;
        });
    }


}
