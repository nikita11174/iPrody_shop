package ru.iprody.deliveryservice.integration.order.messaging.dto;

import java.math.BigDecimal;

public record OrderPaidMessage(
        Long orderId,
        BigDecimal amount,
        String currency
) {
}
