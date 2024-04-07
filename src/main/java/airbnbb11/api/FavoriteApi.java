package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.response.FavoriteHousesResponse;
import airbnbb11.dto.response.FavoritesResponse;
import airbnbb11.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Favorite API", description = "API for favorites")
public class FavoriteApi {

    private final FavoriteService favoriteService;

    @Operation(summary = "Get  all user's favorite houses", description = "Get  all user's favorite houses")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/getAllFavorites")
    public List<FavoriteHousesResponse> getAllFavorites(){
        return favoriteService.getAllFavoriteHouses();
    }

    @Operation(summary = "Add or remove House from favorites.", description = "Add or remove Houses from favorites.")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/{houseId}")
    public Response addOrRemoveFavorite(@PathVariable("houseId") Long houseId) {
        return favoriteService.addOrRemoveFavorite(houseId);
    }

    @Operation(summary = "Get all favorites by house id",
            description = "Get all favorites by house id/User only")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public List<FavoritesResponse> getAllFavoritesByHouseId(@RequestParam Long houseId) {
        return favoriteService.getAllFavoritesByHouseId(houseId);
    }

}
