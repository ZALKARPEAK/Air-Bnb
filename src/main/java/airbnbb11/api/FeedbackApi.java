package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.FeedbackRequest;
import airbnbb11.dto.response.FeedbackResponse;
import airbnbb11.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Feedback API", description = "API for feedbacks")
public class FeedbackApi {

    private final FeedbackService feedbackService;

    @Operation(summary = "Get house rating statistics",
            description = "Get the average rating, number of ratings, and the distribution of ratings for a house",
            tags = {"feedbacks"})
    @GetMapping("/{houseId}/ratings")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public Map<String, Object> getHouseRatingStatus(@PathVariable Long houseId) {
        return feedbackService.calculateRatingStatus(houseId);
    }

    @Operation(summary = "Save feedback",
            description = "Save feedback for a house",
            tags = {"feedbacks"})
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public Response saveFeedback(@RequestParam long houseId,@RequestBody @Valid FeedbackRequest request) {
        return feedbackService.saveFeedback(houseId,request);
    }

    @Operation(summary = "Get all feedbacks by house id",
            description = "Get all feedbacks for a house",
            tags = {"feedbacks"})
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{houseId}")
    public List<FeedbackResponse> getAllFeedbacksByHouseId(@PathVariable Long houseId) {
        return feedbackService.getAllFeedbacksByHouseId(houseId);
    }

    @Operation(summary = "Like or dislike a feedback",
            description = "Like or dislike a feedback / Remove like or dislike from a feedback",
            tags = {"feedbacks"})
    @PostMapping("/reaction")
    public Response reactionToFeedback(@RequestParam long feedbackId, String reaction) {
        return feedbackService.reactionToFeedback(feedbackId, reaction);
    }

    @Operation(summary = "Delete feedback",
            description = "Delete feedback by id",
            tags = {"feedbacks"})
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{feedbackId}")
    public Response deleteFeedback(@PathVariable long feedbackId) {
        return feedbackService.deleteFeedback(feedbackId);
    }

    @Operation(summary = "Update feedback",
            description = "Update feedback by id / Only the user who posted the feedback can update it" ,
            tags = {"feedbacks"})
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/update/{feedbackId}")
    public Response updateFeedback(@PathVariable long feedbackId, @RequestBody @Valid FeedbackRequest request) {
        return feedbackService.updateFeedback(feedbackId, request);
    }


}
