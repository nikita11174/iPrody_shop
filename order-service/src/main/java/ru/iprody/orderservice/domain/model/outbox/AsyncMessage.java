package ru.iprody.orderservice.domain.model.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "async_messages")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "topic"}, callSuper = false)
@IdClass(AsyncMessageId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncMessage extends PersistableEntity<AsyncMessageId> {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Id
    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "headers")
    private String headers;

    @Column(name = "val", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AsyncMessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AsyncMessageStatus status;

    @Override
    @Transient
    public AsyncMessageId getId() {
        return new AsyncMessageId(id, topic);
    }
}
