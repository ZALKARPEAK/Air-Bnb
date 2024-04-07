package airbnbb11.repository.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;
@Repository
public interface FeedbackDao {
    Map<String, Object> calculateRatingStats(Long houseId);
}
