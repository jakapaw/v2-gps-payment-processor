package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Data
public class InvoiceEntity extends AbstractEntity<String> {

    @Id @Setter
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

    @OneToMany(mappedBy = "invoiceId")
    List<PaymentEvent> paymentEvents;

    @Override
    public String getId() {
        return id;
    }
}
