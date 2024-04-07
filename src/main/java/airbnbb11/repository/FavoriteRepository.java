package airbnbb11.repository;

import airbnbb11.entities.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favourite,Long> {
    List<Favourite> getAllByHouseId(Long houseId);
}
