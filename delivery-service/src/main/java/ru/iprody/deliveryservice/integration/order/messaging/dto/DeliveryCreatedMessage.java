package ru.iprody.deliveryservice.integration.order.messaging.dto;

public record DeliveryCreatedMessage(
        Long orderId,
        Long deliveryId,
        String status
) {
}
