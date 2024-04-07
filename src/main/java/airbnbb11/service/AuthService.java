package airbnbb11.service;

import airbnbb11.dto.response.AuthResponse;
import airbnbb11.dto.request.SignInRequest;
import com.google.firebase.auth.FirebaseAuthException;

public interface AuthService {
    AuthResponse signIn(SignInRequest signInRequest);
    AuthResponse authWithGoogleAccount(String tokenId) throws FirebaseAuthException;
}
