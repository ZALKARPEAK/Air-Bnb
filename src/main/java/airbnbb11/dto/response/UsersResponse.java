package airbnbb11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {
    private long id;
    private String username;
    private String contact;
    private long bookingsQuantity;
    private long housesQuantity;

    public UsersResponse(long userId, String username, String email, int houses) {
        this.id = userId;
        this.username = username;
        this.contact = email;
        this.housesQuantity = houses;
    }
}
