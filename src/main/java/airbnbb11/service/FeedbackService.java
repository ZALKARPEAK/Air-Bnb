package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.FeedbackRequest;
import airbnbb11.dto.response.FeedbackResponse;

import java.util.List;
import java.util.Map;

public interface FeedbackService {
    Map<String, Object> calculateRatingStatus(Long houseId);

    Response saveFeedback(long houseId,FeedbackRequest request);

    List<FeedbackResponse> getAllFeedbacksByHouseId(Long houseId);

    Response reactionToFeedback(long feedbackId, String reaction);

    Response deleteFeedback(long feedbackId);

    Response updateFeedback(long feedbackId, FeedbackRequest request);
}
