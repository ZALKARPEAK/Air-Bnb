package airbnbb11.repository;

import airbnbb11.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {


    @Query("select h from House h where h.user.id =:id")
    List<House> getHousesByUserId(@Param("id") Long userId);

    @Query("select h.images from House h where h.id =:id")
    List<String> getHouseImages(@Param("id") Long houseId);

}