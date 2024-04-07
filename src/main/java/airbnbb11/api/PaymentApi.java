package airbnbb11.api;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.CreatePaymentRequest;
import airbnbb11.service.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Payment API", description = "API for payment management")
public class PaymentApi {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Charge credit card",
            description = "This method executes payments through the Stripe API using the token, amount, and other payment parameters passed in, as well as random exceptions that may be associated with the payment")
    public ResponseEntity<String> chargeCreditCard(@Valid @RequestBody CreatePaymentRequest chargeRequest) {
        try {
            Response response = paymentService.chargeCreditCard(chargeRequest);
            return ResponseEntity.ok(response.message());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment: " + e.getMessage());
        }
    }
}
