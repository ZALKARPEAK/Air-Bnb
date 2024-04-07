package airbnbb11.service.impl;

import airbnbb11.dto.Response;
import airbnbb11.dto.request.CreatePaymentRequest;
import airbnbb11.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    @Override
    public Response chargeCreditCard(CreatePaymentRequest createPaymentRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (createPaymentRequest.getAmount() * 100));
        chargeParams.put("currency", "USD");
        chargeParams.put("source", createPaymentRequest.getToken());
        chargeParams.put("customer",createPaymentRequest.getStripeId());
        Charge charge = Charge.create(chargeParams);
        return Response
                .builder()
                .message(charge.getDescription())
                .build();
    }
}
