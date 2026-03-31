package ru.iprody.orderservice.integration.delivery.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.iprody.orderservice.domain.model.Order;
import ru.iprody.orderservice.integration.delivery.messaging.config.KafkaDeliveryProperties;
import ru.iprody.orderservice.integration.delivery.messaging.dto.OrderPaidMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidPublisher {

    private final KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;
    private final KafkaDeliveryProperties props;

    public void publish(Order order) {
        var message = new OrderPaidMessage(
                order.getId(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency()
        );
        kafkaTemplate.send(props.orderPaidTopic(), order.getId().toString(), message);
        log.info("Published OrderPaidMessage for orderId={}", order.getId());
    }
}
