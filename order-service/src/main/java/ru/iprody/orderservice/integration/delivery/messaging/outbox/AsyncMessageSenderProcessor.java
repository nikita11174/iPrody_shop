package ru.iprody.orderservice.integration.delivery.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.iprody.orderservice.application.outbox.AsyncMessageService;
import ru.iprody.orderservice.common.SendingAsyncMessageException;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.integration.delivery.messaging.dto.OrderPaidMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncMessageSenderProcessor {

    private final AsyncMessageService asyncMessageService;
    private final KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void sendMessage(AsyncMessage message) {
        try {
            OrderPaidMessage payload = objectMapper.readValue(message.getValue(), OrderPaidMessage.class);

            kafkaTemplate.send(message.getTopic(), message.getId().getId(), payload)
                    .exceptionally(e -> {
                        throw new SendingAsyncMessageException(
                                "Error sending outbox message id=%s".formatted(message.getId()), e);
                    })
                    .get();

            asyncMessageService.markAsSent(message);
            log.info("Outbox message sent and marked SENT: id={}, topic={}", message.getId().getId(), message.getTopic());
        } catch (Exception e) {
            throw new SendingAsyncMessageException(
                    "Error sending outbox message id=%s".formatted(message.getId()), e);
        }
    }
}
