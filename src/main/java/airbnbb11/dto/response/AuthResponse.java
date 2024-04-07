package airbnbb11.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String email,
        String role,
        String image

) {
}
