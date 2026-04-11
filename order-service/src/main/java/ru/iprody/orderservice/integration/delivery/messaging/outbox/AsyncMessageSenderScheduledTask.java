package ru.iprody.orderservice.integration.delivery.messaging.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iprody.orderservice.application.outbox.AsyncMessageService;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncMessageSenderScheduledTask {

    private final AsyncMessageService asyncMessageService;
    private final AsyncMessageSenderProcessor processor;

    @Scheduled(fixedDelay = 3000)
    public void sendOutboxMessages() {
        List<AsyncMessage> messages = asyncMessageService.getUnsentOutboxMessages(50);
        if (!messages.isEmpty()) {
            log.debug("Outbox: found {} unsent message(s)", messages.size());
        }
        for (AsyncMessage message : messages) {
            processor.sendMessage(message);
        }
    }
}
