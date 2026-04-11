package ru.iprody.orderservice.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessage;
import ru.iprody.orderservice.domain.model.outbox.AsyncMessageId;

import java.util.List;

public interface AsyncMessageRepository extends JpaRepository<AsyncMessage, AsyncMessageId> {

    @Query("SELECT m FROM AsyncMessage m WHERE m.status = 'CREATED' AND m.type = 'OUTBOX' ORDER BY m.createdAt")
    List<AsyncMessage> findUnsentOutboxMessages(Pageable pageable);
}
