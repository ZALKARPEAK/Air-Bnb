package airbnbb11.api;

import airbnbb11.dto.response.AuthResponse;
import airbnbb11.dto.request.SignInRequest;
import airbnbb11.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Auth API", description = "Auth endpoints")
public class AuthApi {

    private final AuthService authenticationService;

    @Operation(summary = "Sign In / Permission: Public",
            description = "Sign In / Email and Password is required.")
    @PostMapping("/signIn")
    public AuthResponse signIn(@RequestBody SignInRequest request) {
        log.info("Endpoint called: POST /api/auth/signIn");
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Authenticate with Google Account / Permission: Public",
            description = "Authenticate with Google Account/ Google Token Id is required.")
    @PostMapping("/authenticate/google")
    public AuthResponse authWithGoogleAccount(@RequestParam String tokenId) throws FirebaseAuthException {
         log.info("Endpoint called: POST /api/auth/authenticate/google");
        return authenticationService.authWithGoogleAccount(tokenId);
    }
}
