package airbnbb11.dto.house;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationHouseResponse {
    private List<PagHouseResponse> houseResponses;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
