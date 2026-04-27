package ru.iprody.orderservice.domain.model.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;

/**
 * Базовый класс для outbox-сущностей.
 * Реализует Persistable.isNew() через createdAt:
 * если createdAt == null — запись новая, JPA выполнит INSERT (не SELECT + UPDATE).
 */
@MappedSuperclass
public abstract class PersistableEntity<T> implements Persistable<T> {

    @Column(name = "created_at", insertable = false, updatable = false)
    @ColumnDefault("now()")
    private OffsetDateTime createdAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
