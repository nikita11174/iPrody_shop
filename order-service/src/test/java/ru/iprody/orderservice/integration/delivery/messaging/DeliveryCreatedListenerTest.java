package ru.iprody.orderservice.integration.delivery.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageStatus;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageType;
import ru.iprody.orderservice.domain.repository.AsyncMessageRepository;
import ru.iprody.orderservice.integration.delivery.messaging.dto.DeliveryCreatedMessage;
import ru.iprody.orderservice.integration.delivery.messaging.outbox.AsyncMessageSenderProcessor;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class DeliveryCreatedListenerTest {

    private static final String STATIC_IDEMPOTENCY_KEY = "00000000-0000-0000-0000-000000000001";
    private static final String TOPIC = "delivery.created";

    @Autowired
    private DeliveryCreatedListener listener;

    @Autowired
    private AsyncMessageRepository asyncMessageRepository;

    @MockBean
    private AsyncMessageSenderProcessor asyncMessageSenderProcessor;

    @BeforeEach
    void setUp() {
        asyncMessageRepository.deleteAll();
    }

    @Test
    void shouldSaveInboxMessageOnFirstConsume() throws Exception {
        ConsumerRecord<String, DeliveryCreatedMessage> record = buildRecord(STATIC_IDEMPOTENCY_KEY);
        Acknowledgment ack = mock(Acknowledgment.class);

        listener.consume(record, record.value(), ack);

        var messages = asyncMessageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getType()).isEqualTo(AsyncMessageType.INBOX);
        assertThat(messages.get(0).getStatus()).isEqualTo(AsyncMessageStatus.RECEIVED);
        assertThat(messages.get(0).getId().getId()).isEqualTo(STATIC_IDEMPOTENCY_KEY);
        verify(ack, times(1)).acknowledge();
    }

    @Test
    void shouldDeduplicateOnSameIdempotencyKey() throws Exception {
        ConsumerRecord<String, DeliveryCreatedMessage> record = buildRecord(STATIC_IDEMPOTENCY_KEY);
        Acknowledgment ack = mock(Acknowledgment.class);

        listener.consume(record, record.value(), ack);
        listener.consume(record, record.value(), ack);

        assertThat(asyncMessageRepository.findAll()).hasSize(1);
        verify(ack, times(2)).acknowledge();
    }

    private ConsumerRecord<String, DeliveryCreatedMessage> buildRecord(String idempotencyKey) {
        DeliveryCreatedMessage message = new DeliveryCreatedMessage(1L, 1L, "CREATED");
        ConsumerRecord<String, DeliveryCreatedMessage> record = new ConsumerRecord<>(TOPIC, 0, 0L, "1", message);
        record.headers().add("X-Idempotency-Key", idempotencyKey.getBytes(StandardCharsets.UTF_8));
        return record;
    }
}
