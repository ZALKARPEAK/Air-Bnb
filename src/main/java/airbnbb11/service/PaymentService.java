package airbnbb11.service;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.CreatePaymentRequest;
import com.stripe.exception.StripeException;

public interface PaymentService {
    Response chargeCreditCard(CreatePaymentRequest createPaymentRequest) throws StripeException;

}
