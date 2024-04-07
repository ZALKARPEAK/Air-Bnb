package airbnbb11.service.impl;

import airbnbb11.config.jwt.JwtService;
import airbnbb11.dto.response.AuthResponse;
import airbnbb11.dto.request.SignInRequest;
import airbnbb11.entities.User;
import airbnbb11.entities.enums.Role;
import airbnbb11.exception.BadCredentialsException;
import airbnbb11.exception.EntityNotFoundException;
import airbnbb11.exception.LockedException;
import airbnbb11.repository.UserRepository;
import airbnbb11.service.AuthService;
import airbnbb11.service.UserService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    public AuthResponse signIn(SignInRequest request) {

        if (request.email().isBlank() || request.password().isBlank()) {
            throw new BadCredentialsException("Email or password is blank");
        }
        User user = userRepository.findUserByEmail(request.email()).orElseThrow(
                () -> {
                    log.warn("Response: User not found");
                    return new EntityNotFoundException("User not found");
                }
        );

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                log.warn("Response: Wrong password");
                throw new BadCredentialsException("wrong credentials");
        }
       if(!user.getRole().equals(Role.ADMIN)) {
           log.warn("Response: User is not admin");
           throw new BadCredentialsException("Only admin can sign in");
       }
            userService.resetFailedLoginAttempts(user.getEmail());
            log.info("User signed in with email: {}", user.getEmail());
            String jwtToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();
 }

    @Override
    public AuthResponse authWithGoogleAccount(String token) {
        log.info("Метод  аутентификации через Google стартовал ");
        FirebaseToken firebaseToken;
        try {
            firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException firebaseAuthException) {
            log.error("Во время аутентификации произошла ошибка !!!");
            throw new IllegalArgumentException("Во время аутентификации произошла ошибка !!!");
        }
        User user = new User();
        if (userRepository.findUserByEmail(firebaseToken.getEmail()).isEmpty()) {
            String fullName = firebaseToken.getName();
            int spaceIndex = fullName.indexOf(" ");
            if (spaceIndex != -1) {
                String firstName = fullName.substring(0, spaceIndex);
                String lastName = fullName.substring(spaceIndex + 1);
                user.setFirstName(firstName);
                user.setLastName(lastName);
            } else {
                user.setFirstName(fullName);
            }
            user.setEmail(firebaseToken.getEmail());
            user.setPassword(passwordEncoder.encode(firebaseToken.getEmail()));
            user.setRole(Role.USER);
            user.setImage(firebaseToken.getPicture());
            userRepository.save(user);
            log.info("Пользователь удачно сохранен");
        }
        user = userRepository.findUserByEmail(firebaseToken.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с такими :" + firebaseToken.getEmail() + " не существует ."));
        String userAccountToken = jwtService.generateToken(user);
        log.info("Функция  аутентификации через Google удачно завершил работу");
        return new AuthResponse
                (userAccountToken,
                        user.getEmail(),
                        user.getRole().name(),
                        user.getImage());
    }
    @PostConstruct
    public void init() throws IOException{
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource("google.json").getInputStream());
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials).build();
        FirebaseApp.initializeApp(firebaseOptions);
        initSaveAdmin();
    }

    public void initSaveAdmin() {
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("Adminov");
        user.setEmail("admin@gmail.com");
        user.setPassword(passwordEncoder.encode("Admin123"));
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        user.setCreatedDate(java.time.LocalDate.now());
        if (!userRepository.existsByEmail(user.getEmail())) {
            userRepository.save(user);
            log.info("Успешно сохраненен админ методом init!");
        }
    }

}
