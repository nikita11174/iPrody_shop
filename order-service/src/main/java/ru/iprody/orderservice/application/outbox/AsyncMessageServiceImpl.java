package ru.iprody.orderservice.application.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageStatus;
import ru.iprody.orderservice.domain.repository.AsyncMessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncMessageServiceImpl implements AsyncMessageService {

    private final AsyncMessageRepository repository;

    @Override
    @Transactional
    public void saveMessage(AsyncMessage message) {
        repository.save(message);
    }

    @Override
    public List<AsyncMessage> getUnsentOutboxMessages(int batchSize) {
        return repository.findUnsentOutboxMessages(Pageable.ofSize(batchSize));
    }

    @Override
    @Transactional
    public void markAsSent(AsyncMessage message) {
        message.setStatus(AsyncMessageStatus.SENT);
        repository.save(message);
    }
}
