package ru.iprody.orderservice.integration.delivery.messaging.dto;

public record DeliveryCreatedMessage(Long orderId, Long deliveryId, String status) {
}
