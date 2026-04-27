package ru.iprody.orderservice.application.outbox;

import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;

import java.util.List;

public interface AsyncMessageService {

    void saveMessage(AsyncMessage message);

    List<AsyncMessage> getUnsentOutboxMessages(int batchSize);

    void markAsSent(AsyncMessage message);
}
