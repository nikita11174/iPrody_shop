package ru.iprody.orderservice.integration.delivery.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.iprody.orderservice.application.outbox.AsyncMessageService;
import ru.iprody.orderservice.common.SendingAsyncMessageException;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageId;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageStatus;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageType;
import ru.iprody.orderservice.integration.delivery.messaging.dto.OrderPaidMessage;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncMessageSenderProcessorTest {

    @Mock
    private AsyncMessageService asyncMessageService;

    @SuppressWarnings("unchecked")
    @Mock
    private KafkaTemplate<String, OrderPaidMessage> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AsyncMessageSenderProcessor processor;

    @Test
    void shouldSendMessageToKafkaAndMarkAsSent() throws Exception {
        String messageId = UUID.randomUUID().toString();
        String topic = "order.paid";
        String payload = """
                {"orderId":42,"amount":83970.00,"currency":"RUB"}
                """;

        AsyncMessage message = AsyncMessage.builder()
                .id(messageId)
                .topic(topic)
                .value(payload)
                .type(AsyncMessageType.OUTBOX)
                .status(AsyncMessageStatus.CREATED)
                .build();

        given(kafkaTemplate.send(eq(topic), eq(messageId), any(OrderPaidMessage.class)))
                .willReturn(CompletableFuture.completedFuture(null));

        processor.sendMessage(message);

        ArgumentCaptor<OrderPaidMessage> captor = ArgumentCaptor.forClass(OrderPaidMessage.class);
        verify(kafkaTemplate).send(eq(topic), eq(messageId), captor.capture());
        OrderPaidMessage sent = captor.getValue();
        Assertions.assertThat(sent.orderId()).isEqualTo(42L);
        Assertions.assertThat(sent.amount()).isEqualByComparingTo(new BigDecimal("83970.00"));
        Assertions.assertThat(sent.currency()).isEqualTo("RUB");

        verify(asyncMessageService).markAsSent(message);
    }

    @Test
    void shouldThrowWhenKafkaFails() {
        String messageId = UUID.randomUUID().toString();
        AsyncMessage message = AsyncMessage.builder()
                .id(messageId)
                .topic("order.paid")
                .value("{\"orderId\":1,\"amount\":100,\"currency\":\"RUB\"}")
                .type(AsyncMessageType.OUTBOX)
                .status(AsyncMessageStatus.CREATED)
                .build();

        CompletableFuture<SendResult<String, OrderPaidMessage>> failed =
                CompletableFuture.failedFuture(new RuntimeException("Kafka unavailable"));
        given(kafkaTemplate.send(any(), any(), any())).willReturn(failed);

        Assertions.assertThatThrownBy(() -> processor.sendMessage(message))
                .isInstanceOf(SendingAsyncMessageException.class);
    }
}
