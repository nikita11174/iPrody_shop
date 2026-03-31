package ru.iprody.orderservice.integration.delivery.messaging.dto;

import java.math.BigDecimal;

public record OrderPaidMessage(
        Long orderId,
        BigDecimal amount,
        String currency
) {
}
