package ru.iprody.paymentservice.web.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.iprody.paymentservice.domain.model.PaymentMethod;
import ru.iprody.paymentservice.domain.model.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private PaymentStatus status;
    private PaymentMethod method;
    private PaymentAmountDto amount;
    private LocalDateTime createdAt;
}
