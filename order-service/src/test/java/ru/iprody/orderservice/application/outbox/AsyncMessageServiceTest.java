package ru.iprody.orderservice.application.outbox;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageStatus;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageType;
import ru.iprody.orderservice.domain.repository.AsyncMessageRepository;
import ru.iprody.orderservice.integration.delivery.messaging.outbox.AsyncMessageSenderProcessor;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class AsyncMessageServiceTest {

    @Autowired
    private AsyncMessageService asyncMessageService;

    @Autowired
    private AsyncMessageRepository asyncMessageRepository;

    @MockBean
    private AsyncMessageSenderProcessor asyncMessageSenderProcessor;

    @BeforeEach
    void setUp() {
        asyncMessageRepository.deleteAll();
    }

    @Test
    void shouldSaveMessage() {
        AsyncMessage message = buildMessage(AsyncMessageStatus.CREATED);

        asyncMessageService.saveMessage(message);

        List<AsyncMessage> all = asyncMessageRepository.findAll();
        Assertions.assertThat(all).hasSize(1);
        Assertions.assertThat(all.get(0).getStatus()).isEqualTo(AsyncMessageStatus.CREATED);
        Assertions.assertThat(all.get(0).getType()).isEqualTo(AsyncMessageType.OUTBOX);
        Assertions.assertThat(all.get(0).getTopic()).isEqualTo("order.paid");
    }

    @Test
    void shouldReturnOnlyUnsentOutboxMessages() {
        AsyncMessage created = buildMessage(AsyncMessageStatus.CREATED);
        AsyncMessage sent = buildMessage(AsyncMessageStatus.SENT);

        asyncMessageService.saveMessage(created);
        asyncMessageService.saveMessage(sent);

        List<AsyncMessage> unsent = asyncMessageService.getUnsentOutboxMessages(50);

        Assertions.assertThat(unsent).hasSize(1);
        Assertions.assertThat(unsent.get(0).getStatus()).isEqualTo(AsyncMessageStatus.CREATED);
    }

    @Test
    void shouldMarkMessageAsSent() {
        AsyncMessage message = buildMessage(AsyncMessageStatus.CREATED);
        asyncMessageService.saveMessage(message);

        // получаем из БД — createdAt заполнен, isNew() = false → UPDATE сработает корректно
        AsyncMessage fromDb = asyncMessageRepository.findAll().get(0);
        asyncMessageService.markAsSent(fromDb);

        AsyncMessage updated = asyncMessageRepository.findById(fromDb.getId()).orElseThrow();
        Assertions.assertThat(updated.getStatus()).isEqualTo(AsyncMessageStatus.SENT);
    }

    private AsyncMessage buildMessage(AsyncMessageStatus status) {
        return AsyncMessage.builder()
                .id(UUID.randomUUID().toString())
                .topic("order.paid")
                .value("{\"orderId\":1,\"amount\":50000,\"currency\":\"RUB\"}")
                .type(AsyncMessageType.OUTBOX)
                .status(status)
                .build();
    }
}
