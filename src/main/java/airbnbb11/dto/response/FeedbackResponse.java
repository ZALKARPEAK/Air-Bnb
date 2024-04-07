package airbnbb11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {
    private Long id;
    private String name;
    private String feedback;
    private int rating;
    private List<String> images;
    private int likes;
    private int dislikes;
    private String userImage;
    private String postedAt;

}
