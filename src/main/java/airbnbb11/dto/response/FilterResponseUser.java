package airbnbb11.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
public class FilterResponseUser {
    private List<FilterResponse> responses;

    public FilterResponseUser(List<FilterResponse> responses) {
        this.responses = responses;
    }
}
