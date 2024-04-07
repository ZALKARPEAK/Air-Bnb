package airbnbb11.dto.request;

import airbnbb11.validation.EmailValidation;
import airbnbb11.validation.PasswordValidation;
import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank(message = "Email is mandatory")
        String email,
        @NotBlank(message = "Password is mandatory")
        @PasswordValidation
        String password
) {
}
