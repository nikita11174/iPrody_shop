package ru.iprody.deliveryservice.integration.order.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.iprody.deliveryservice.domain.model.Delivery;
import ru.iprody.deliveryservice.domain.model.DeliveryAddress;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;
import ru.iprody.deliveryservice.domain.model.TimeWindow;
import ru.iprody.deliveryservice.domain.repository.DeliveryRepository;
import ru.iprody.deliveryservice.integration.order.messaging.config.KafkaOrderProperties;
import ru.iprody.deliveryservice.integration.order.messaging.dto.DeliveryCreatedMessage;
import ru.iprody.deliveryservice.integration.order.messaging.dto.OrderPaidMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidListener {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, DeliveryCreatedMessage> kafkaTemplate;
    private final KafkaOrderProperties props;

    @KafkaListener(topics = "${kafka.order.order-paid-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderPaid(OrderPaidMessage message) {
        log.info("Received OrderPaidMessage for orderId={}", message.orderId());

        Delivery delivery = new Delivery(
                message.orderId(),
                DeliveryStatus.CREATED,
                new DeliveryAddress("TBD", "TBD", "000000", "RU"),
                LocalDate.now().plusDays(3),
                new TimeWindow(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                UUID.randomUUID().toString()
        );
        Delivery saved = deliveryRepository.save(delivery);
        log.info("Created Delivery id={} for orderId={}", saved.getId(), message.orderId());

        var result = new DeliveryCreatedMessage(
                message.orderId(),
                saved.getId(),
                saved.getStatus().name()
        );
        kafkaTemplate.send(MessageBuilder
                .withPayload(result)
                .setHeader(KafkaHeaders.TOPIC, props.deliveryCreatedTopic())
                .setHeader(KafkaHeaders.KEY, message.orderId().toString())
                .setHeader("X-Idempotency-Key", saved.getId().toString())
                .build());
        log.info("Published DeliveryCreatedMessage for orderId={}", message.orderId());
    }
}
