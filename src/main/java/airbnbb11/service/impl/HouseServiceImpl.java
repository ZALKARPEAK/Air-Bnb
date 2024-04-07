package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.house.AllHousesResponse;
import airbnbb11.dto.house.HouseRequest;
import airbnbb11.dto.house.HouseResponse;
import airbnbb11.dto.mappers.HouseMapper;
import airbnbb11.dto.house.PaginationHouseResponse;
import airbnbb11.dto.response.*;
import airbnbb11.entities.Address;
import airbnbb11.entities.House;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.HouseStatus;
import airbnbb11.entities.enums.HouseType;
import airbnbb11.entities.enums.Region;
import airbnbb11.entities.enums.Role;
import airbnbb11.exception.BadCredentialsException;
import airbnbb11.exception.EntityNotFoundException;
import airbnbb11.repository.AddressRepository;
import airbnbb11.repository.HouseRepository;
import airbnbb11.repository.dao.HouseDao;
import airbnbb11.repository.dao.UserDao;
import airbnbb11.repository.UserRepository;
import airbnbb11.service.HouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HouseServiceImpl implements HouseService {

    private final UserDao userDao;
    private final HouseMapper houseMapper;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final HouseDao houseDao;
    private final HouseRepository houseRepository;
    private final AddressRepository addressRepository;
    @Value("${google.api.key}")
    private String google_api_key;

    private RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }


//    public airbnbb11.dto.response.LatLng getGeoCoordinateForAddress(String address) throws BadRequestException {
//        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + google_api_key;
//        GeocodingResponse response = restTemplate().getForObject(url, GeocodingResponse.class);
//        if (response != null && response.getResults().size() > 0) {
//            GeocodingResult result = response.getResults().get(0);
//            return result.getGeometry().getLocation();
//        } else throw new BadRequestException("Address not found");
//    }

    @Override
    public Response saveHouse(HouseRequest houseRequest) throws BadRequestException {
//
//        LatLng coordinate = getGeoCoordinateForAddress(houseRequest.address());
//        double latitude = coordinate.getLat();
//        double longitude = coordinate.getLng();
        User user = userService.findByAuth();
        log.info("User found");


        Address address = Address.builder()
                .province(houseRequest.province())
                .address(houseRequest.address())
                .region(houseRequest.region())
//                .longitude(longitude)
//                .latitude(latitude)
                .build();

        addressRepository.save(address);

        houseRepository.save(House.builder()
                .title(houseRequest.title())
                .description(houseRequest.description())
                .houseType(houseRequest.houseType())
                .createdDate(LocalDate.now(ZoneId.of("Asia/Bishkek")))
                .createdTime(LocalDateTime.now())
                .price(houseRequest.price())
                .houseStatus(HouseStatus.MODERATING)
                .maxGuests(houseRequest.maxOfGuests())
                .address(address)
                .images(houseRequest.images())
                .user(user)
                .build());

        log.info("House announcement successfully saved!");
        return new Response("Announcement successfully saved!");
    }

    @Override
    public Response updateHouse(long houseId, HouseRequest houseRequest) throws BadRequestException {
        User user = userService.findByAuth();
        boolean b = user.getHouses().stream().anyMatch(h -> h.getId() == houseId);
        if (!b) {
            throw new BadRequestException("User has no house with such an id!");
        }
        House house = houseRepository.findById(houseId).orElseThrow(() -> new EntityNotFoundException("House not found!")
        );
        Address address = house.getAddress();
        address.setAddress(houseRequest.address());
        address.setRegion(houseRequest.region());
        address.setProvince(houseRequest.province());
        addressRepository.save(address);
        house.setHouseType(houseRequest.houseType());
        house.setHouseName(houseRequest.title());
        house.setImages(houseRequest.images());
        house.setAddress(address);
        house.setDescription(houseRequest.description());
        house.setMaxGuests(houseRequest.maxOfGuests());
        house.setPrice(houseRequest.price());
        houseRepository.save(house);
        return new Response("House successfully updated!");
    }

    @Override
    public HouseResponse findHouseById(Long id) {
        User user = userService.findByAuth();
        House house = houseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("House not found!")
        );
        boolean booked = user.getBookings().stream().anyMatch(b -> Objects.equals(b.getHouse().getId(), id));
        boolean fav = user.getFavorites().stream().anyMatch(f -> Objects.equals(f.getHouse().getId(), id));
        return new HouseResponse(house, house.getAddress().getAddress(), house.getAddress().getProvince(), booked, fav);
    }

    @Override
    public Response deleteHouseById(Long id) {

        User auth = userService.findByAuth();
        House house = houseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("House not found!")
        );
        if (auth.getRole().equals(Role.ADMIN)) {
            User user = userRepository.getUserByHouseId(id);
            user.getHouses().remove(house);
            userRepository.save(user);
            houseRepository.delete(house);
            return new Response("House successfully deleted!");
        } else if (auth.getRole().equals(Role.USER)) {
            auth.getHouses().remove(house);
            userRepository.save(auth);
            houseRepository.delete(house);
            return new Response("House successfully deleted!");
        }
        throw new BadCredentialsException("You can't delete this announcement!");
    }

    @Override
    public List<AllHousesResponse> getAllHouses() {
        List<House> houses = houseRepository.findAll();
        return houses.stream().
                map(houseMapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserHouseResponse> getUserHouses(Long userId) {

        userService.existsUserId(userId);

        System.out.println("service");
        return userDao.findUserHouses(userId);
    }

    @Override
    public List<UserBookingResponse> getUserBookings(Long userId) {
        userService.existsUserId(userId);
        return userDao.findUserBookings(userId);
    }


    @Override
    public Response blockedHouseById(Long houseId, boolean blockOrUnblock) {
        House house = houseRepository.findById(houseId).orElseThrow(
                () -> new EntityNotFoundException("House with id: " + houseId + " does not exist!"));
        if (blockOrUnblock) {
            if (house.getHouseStatus() == HouseStatus.BLOCKED) {
                house.setHouseStatus(HouseStatus.ACCEPTED);
                return Response
                        .builder()
                        .message("House Unblocked!!!")
                        .build();
            } else {
                return Response
                        .builder()
                        .message("House is not currently blocked!")
                        .build();
            }
        } else {
            if (house.getHouseStatus() == HouseStatus.ACCEPTED) {
                house.setHouseStatus(HouseStatus.BLOCKED);
                return Response
                        .builder()
                        .message("House Blocked!!!")
                        .build();
            }
            return Response
                    .builder()
                    .message("House item not accepted or pending !!!")
                    .build();
        }

    }

    public Response blockedAllHouses(Long userId, boolean blockOrUnblock) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + userId + " does not exist!"));
        List<House> houses = houseRepository.getHousesByUserId(user.getId());
        if (houses.isEmpty()) {
            throw new EntityNotFoundException("This user currently has no ads");
        }
        for (House house : houses) {
            if (blockOrUnblock) {
                if (house.getHouseStatus() == HouseStatus.BLOCKED) {
                    house.setHouseStatus(HouseStatus.ACCEPTED);
                }
            } else {
                if (house.getHouseStatus() == HouseStatus.ACCEPTED) {
                    house.setHouseStatus(HouseStatus.BLOCKED);
                }
            }
        }
        if (blockOrUnblock) {
            return Response
                    .builder()
                    .message("All announcements are unblocked!!!")
                    .build();
        } else {
            return Response
                    .builder()
                    .message("All announcements are blocked!!!")
                    .build();
        }
    }

    @SneakyThrows
    @Override
    public PaginationHouseResponse filteredHouses(Region region, HouseType houseType, String rating, String price, int currentPage, int pageSize) {
        User user = userService.findByAuth();
        if (user.getRole().equals(Role.USER)) {
            return houseDao.getAllHousesFilter(user.getId(), region, houseType, rating, price, currentPage, pageSize);
        }
        throw new BadRequestException("Only Users can access this method!");
    }

    @Override
    public List<AllHousesResponse> getHousesByFilter(String status, HouseType houseType, String rating, String price) {
        return houseDao.getHousesByFilter(status, houseType, rating, price);
    }

    @Override
    public PaginationHouseResponse getAllApplications(int currentPage, int pageSize) {
        return houseDao.getAllApplications(currentPage, pageSize);

    }


    @Override
    public GlobalSearchResponse search(String word, boolean isNearby, Double latitude, Double longitude) {
        GlobalSearchResponse search = houseDao.search(word, isNearby, latitude, longitude);
        for (HouseSearchResponse h : search.getHouseResponses()) {
            List<String> houseImages = houseRepository.getHouseImages(h.getId());
            HouseImagesResponse houseImagesResponse = new HouseImagesResponse();
            houseImagesResponse.setImages(houseImages);
            h.setImages(houseImages);
        }
        return search;
    }


    @Override
    public Response approveApplication(long id, String value, String messageFromAdminToUser) {
        House house = houseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("House not found!"));
        User user = house.getUser();
        switch (value) {
            case "APPROVE" -> {
                house.setHouseStatus(HouseStatus.ACCEPTED);
                houseRepository.save(house);
                return new Response("Application successfully approved!");
            }
            case "BLOCK" -> {
                house.setHouseStatus(HouseStatus.BLOCKED);
                houseRepository.save(house);
                return new Response("Application successfully blocked!");
            }
            case "REJECT" -> {
                house.setHouseStatus(HouseStatus.REJECTED);
                houseRepository.save(house);
                return new Response("Application successfully rejected!");
            }
        }
        user.getMessagesFromAdmin().add(messageFromAdminToUser);
        userRepository.save(user);

        throw new BadCredentialsException("Invalid value!");
    }

    @Override
    public LatestAnnouncementResponse getLatestAnnouncement() {
        return houseDao.getLatestAnnouncement();
    }

    @Override
    public List<PopularHouseResponse> getPopularHouses() {
        return houseDao.getPopularHouses();
    }

    @Override
    public PopularApartmentResponse getPopularApartment() {
        return houseDao.getPopularApartment();
    }
}
