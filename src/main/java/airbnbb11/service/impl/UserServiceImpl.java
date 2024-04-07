package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.response.*;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.PriceType;
import airbnbb11.entities.enums.Role;
import airbnbb11.exception.BadCredentialsException;
import airbnbb11.exception.EntityNotFoundException;
import airbnbb11.repository.UserRepository;
import airbnbb11.repository.dao.UserDao;
import airbnbb11.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserRepository userRepository;
    private static final long LOCK_TIME_DURATION = 30000;

    @Override
    public void increaseFailedLoginAttempts(User user) {
        Objects.requireNonNull(user);
        int currentFailedLoginAttempts = user.getFailedLoginAttempts() + 1;
        log.warn("Response: currentFailedLoginAttempts: " + currentFailedLoginAttempts);
        userRepository.updateFailedLoginAttempts(user.getUsername(), currentFailedLoginAttempts);
    }

    @Override
    public void resetFailedLoginAttempts(String email) {
        Objects.requireNonNull(email);
        log.warn("Response: resetFailedLoginAttempts");
        userRepository.updateFailedLoginAttempts(email, 0);
    }

    @Override
    public void lockUser(String email) {
        userRepository.findUserByEmail(email).ifPresent(user -> {
            user.setAccountNonLocked(false);
            user.setLockTime(new java.sql.Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        });
    }

    @Override
    public boolean unlockUser(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Date lockTime = user.getLockTime();
            if (lockTime == null) {
                return true;
            }
            long lockTimeMillis = lockTime.getTime();
            long currentTime = System.currentTimeMillis();
            if (lockTimeMillis + LOCK_TIME_DURATION < currentTime) {
                user.setAccountNonLocked(true);
                user.setLockTime(null);
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
                log.info("User account unlocked successfully: {}", email);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<UsersResponse> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public UserProfileResponse getUserProfile() {
        User user = findByAuth();
        String fullName = user.getFirstName() + " " + user.getLastName();
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .name(fullName)
                .image(user.getImage())
                .build();
    }

    @Override
    public List<UserHouseResponse> getUsersOnModerationHouses() {
        User user = findByAuth();
        return userDao.findUserModerationHouses(user.getId());
    }

    @Override
    public UserProfileResponse getByIdUser(Long userId) {
        return userDao.getByIdUser(userId);
    }

    @Override
    public Response deleteUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id: " + userId + " doesn't exist!"));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new BadCredentialsException("you can't remove the admin!!");
        }

        userRepository.delete(user);
        log.info("User with id: {} successfully deleted", userId);
        return Response
                .builder()
                .message(String.format("User with id: %s is successfully deleted", userId))
                .build();
    }

    @Override
    public FilterResponseUser getAllAnnouncementsFilters(HouseType houseType, String rating, PriceType price) {
        return userDao.getAllAnnouncementsFilters(houseType, rating, price);
    }

    @Override
    public User findByAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findUserByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("User with email: " + authentication.getName() + " not found!")
        );
    }

    public void existsUserId(Long userId) {

        if (!userRepository.existsById(userId)) {

            throw new EntityNotFoundException("Пользователь не найден!");
        }
    }

    @Override
    public List<UserHouseResponse> getUserHouses() {
        User user = findByAuth();
        return userDao.findUserHouses(user.getId());
    }

    @Override
    public List<UserBookingResponse> getUserBookings() {
        User user = findByAuth();
        return userDao.findUserBookings(user.getId());
    }
}
