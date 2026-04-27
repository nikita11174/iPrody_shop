package ru.iprody.orderservice.integration.delivery.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.Acknowledgment;
import ru.iprody.orderservice.application.outbox.AsyncMessageService;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageStatus;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageType;

@Slf4j
@RequiredArgsConstructor
public abstract class IdempotentKafkaListener<T> {

    protected final AsyncMessageService asyncMessageService;
    protected final ObjectMapper objectMapper;

    public void consume(ConsumerRecord<String, T> record, T message, Acknowledgment ack)
            throws JsonProcessingException {

        Header header = record.headers().lastHeader("X-Idempotency-Key");
        String idempotencyKey = new String(header.value());

        AsyncMessage inboxMessage = AsyncMessage.builder()
                .id(idempotencyKey)
                .topic(record.topic())
                .value(objectMapper.writeValueAsString(message))
                .type(AsyncMessageType.INBOX)
                .status(AsyncMessageStatus.RECEIVED)
                .build();

        try {
            asyncMessageService.saveMessage(inboxMessage);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate message, idempotency key already processed: {}", idempotencyKey);
            ack.acknowledge();
            return;
        }

        processConsumedMessage(message);
        ack.acknowledge();
    }

    protected abstract void processConsumedMessage(T message);
}
