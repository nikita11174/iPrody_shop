package ru.iprody.orderservice.integration.delivery.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.delivery")
public record KafkaDeliveryProperties(
        String orderPaidTopic
) {
}
