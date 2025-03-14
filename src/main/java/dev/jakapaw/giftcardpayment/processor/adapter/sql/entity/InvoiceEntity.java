package dev.jakapaw.giftcardpayment.processor.adapter.sql.entity;

import dev.jakapaw.giftcardpayment.processor.adapter.sql.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Data
@Table(name = "invoice")
public class InvoiceEntity extends AbstractEntity<String> {

    @Id
    @Setter
    @Getter
    String id;

    @Transient
    private boolean isNew = true;

    @NotEmpty
    String buyerId;
    @NotEmpty
    String paymentType;
    @NotEmpty
    String sellerId;
    @PositiveOrZero
    Long totalBill;
    @NotEmpty
    String products;
    @NotEmpty
    String paymentStatus;
    @NotNull
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoice")
    List<PaymentEvent> paymentEvents;

}
