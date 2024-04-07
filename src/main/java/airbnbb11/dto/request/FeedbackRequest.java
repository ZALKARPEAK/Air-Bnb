package airbnbb11.dto.request;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest{
      private   List<String> images;
      @Max(value = 5,message = "Maximum score 5")
      private int rating;
      private String feedback;
}
