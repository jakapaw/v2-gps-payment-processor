package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@EqualsAndHashCode
public abstract class AbstractEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PrePersist
    void markNotNew() {
        this.isNew = false;
    }
}
