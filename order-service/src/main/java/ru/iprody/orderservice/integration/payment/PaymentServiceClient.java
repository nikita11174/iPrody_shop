package ru.iprody.orderservice.integration.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateRequest;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateResponse;

@FeignClient(name = "payment-service-client", url = "${clients.payment-service.url}")
public interface PaymentServiceClient {

    @PostMapping("/api/payments")
    PaymentCreateResponse createPayment(@RequestBody PaymentCreateRequest paymentCreateRequest);
}
