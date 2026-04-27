package ru.iprody.orderservice.integration.delivery.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.iprody.orderservice.application.outbox.AsyncMessageService;
import ru.iprody.orderservice.integration.delivery.messaging.dto.DeliveryCreatedMessage;

@Slf4j
@Component
public class DeliveryCreatedListener extends IdempotentKafkaListener<DeliveryCreatedMessage> {

    public DeliveryCreatedListener(AsyncMessageService asyncMessageService, ObjectMapper objectMapper) {
        super(asyncMessageService, objectMapper);
    }

    @KafkaListener(
            topics = "${kafka.delivery.delivery-created-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Override
    public void consume(ConsumerRecord<String, DeliveryCreatedMessage> record,
                        DeliveryCreatedMessage message,
                        Acknowledgment ack) throws JsonProcessingException {
        super.consume(record, message, ack);
    }

    @Override
    protected void processConsumedMessage(DeliveryCreatedMessage message) {
        log.info("Delivery created: orderId={}, deliveryId={}, status={}",
                message.orderId(), message.deliveryId(), message.status());
    }
}
