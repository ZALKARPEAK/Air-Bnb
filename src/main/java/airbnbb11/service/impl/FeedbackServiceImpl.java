package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.FeedbackRequest;
import airbnbb11.dto.response.FeedbackResponse;
import airbnbb11.entities.Feedback;
import airbnbb11.entities.House;
import airbnbb11.entities.User;
import airbnbb11.repository.FeedbackRepository;
import airbnbb11.repository.HouseRepository;
import airbnbb11.repository.dao.FeedbackDao;
import airbnbb11.service.FeedbackService;
import airbnbb11.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final HouseRepository houseRepository;
    private final UserService userService;
    private final FeedbackDao feedbackDao;

    @Override
    public Map<String, Object> calculateRatingStatus(Long houseId) {
        return feedbackDao.calculateRatingStats(houseId);
    }

    @Override
    @Transactional
    public Response saveFeedback(long houseId, FeedbackRequest request) {
        User user = userService.findByAuth();
        log.info("house pre finding");
        House house = houseRepository.findById(houseId).orElseThrow(()-> new EntityNotFoundException("House Not found"));
        log.info("house found");
        Feedback feedback = new Feedback(house, user, request.getFeedback(), request.getRating(), request.getImages(), LocalDate.now());
        feedbackRepository.save(feedback);
        return Response.builder().message("Feedback saved").build();
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacksByHouseId(Long houseId) {
        List<Feedback> feedbacks = feedbackRepository.findFeedbacksByHouseId(houseId);
        return feedbacks.stream().map(feedback -> FeedbackResponse.builder()
                .id(feedback.getId())
                .name(feedback.getUser().getFirstName()+" "+feedback.getUser().getLastName())
                .feedback(feedback.getFeedback())
                .rating(feedback.getRating())
                .images(feedback.getImages())
                .likes(feedback.getLikes().size())
                .dislikes(feedback.getDislikes().size())
                .userImage(feedback.getUser().getImage())
                .postedAt(feedback.getPostedAt().toString())
                .build()).toList();
    }

    @Override
    public Response reactionToFeedback(long feedbackId, String reaction) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(EntityNotFoundException::new);
        User user = userService.findByAuth();
        if(reaction.equals("like")) {
            if (feedback.getLikes().contains(user)) {
                feedback.getLikes().remove(user);
                feedbackRepository.save(feedback);
                return Response.builder().message("Like removed").build();
            } else {
                if (feedback.getDislikes().contains(user)) {
                    feedback.getDislikes().remove(user);
                    feedbackRepository.save(feedback);
                }
                feedback.getLikes().add(user);
            }
        } else {
            if (feedback.getDislikes().contains(user)) {
                feedback.getDislikes().remove(user);
                feedbackRepository.save(feedback);
                return Response.builder().message("Dislike removed").build();
            } else {
                if (feedback.getLikes().contains(user)) {
                    feedback.getLikes().remove(user);
                    feedbackRepository.save(feedback);
                }
                feedback.getDislikes().add(user);
            }
        }
        feedbackRepository.save(feedback);
        log.info("Reaction saved for feedback with ID: {}", feedbackId);
        return Response.builder().message("Reaction saved").build();
    }

    @Override
    public Response deleteFeedback(long feedbackId) {
        User user = userService.findByAuth();
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(EntityNotFoundException::new);
        if(feedback.getUser().getId().equals(user.getId()) || feedback.getHouse().getUser().getId().equals(user.getId())) {
            feedbackRepository.delete(feedback);
            return Response.builder().message("Feedback deleted").build();
        }
        log.info("Feedback successfully deleted!");
    return Response.builder().message("You are not allowed to delete this feedback").build();
    }

    @Override
    public Response updateFeedback(long feedbackId, FeedbackRequest request) {
        User user = userService.findByAuth();
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(EntityNotFoundException::new);
        if(feedback.getUser().getId().equals(user.getId())) {
            feedback.setFeedback(request.getFeedback());
            feedback.setRating(request.getRating());
            feedback.setImages(request.getImages());
            feedbackRepository.save(feedback);
            return Response.builder().message("Feedback updated").build();
        }
        log.info("Feedback successfully updated!");
        return Response.builder().message("You are not allowed to update this feedback").build();
    }
}
