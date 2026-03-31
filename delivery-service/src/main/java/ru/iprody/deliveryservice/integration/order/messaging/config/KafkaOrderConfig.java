package ru.iprody.deliveryservice.integration.order.messaging.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaOrderProperties.class)
public class KafkaOrderConfig {
}
