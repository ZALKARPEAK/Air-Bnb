package airbnbb11.repository;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);

    @Query("update User u set u.failedLoginAttempts = ?2 where u.email= ?1")
    @Modifying
    void updateFailedLoginAttempts(String email, int failedLoginAttempts);

    @Query("select case when count(u)> 0 then true else false end from User u where u.role = ?1")
    boolean existsByRole(Role role);

    boolean existsByStripeId(String stripe);
    @Query("select u from User u JOIN u.bookings b where b.id=?1")
    User getUserByBookingId(long bookingId);

    @Query("select u from User u JOIN u.houses h where h.id=?1")
    User getUserByHouseId(long houseId);

}
