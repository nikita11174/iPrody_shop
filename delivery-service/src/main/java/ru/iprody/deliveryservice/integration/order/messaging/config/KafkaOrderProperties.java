package ru.iprody.deliveryservice.integration.order.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.order")
public record KafkaOrderProperties(
        String orderPaidTopic,
        String deliveryCreatedTopic
) {
}
