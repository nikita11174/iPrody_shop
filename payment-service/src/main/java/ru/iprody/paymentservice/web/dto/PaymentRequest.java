package ru.iprody.paymentservice.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iprody.paymentservice.domain.model.PaymentMethod;
import ru.iprody.paymentservice.domain.model.PaymentStatus;

@Data
@NoArgsConstructor
public class PaymentRequest {

    private Long orderId;
    private PaymentStatus status;
    private PaymentMethod method;
    private PaymentAmountRequest amount;
}
