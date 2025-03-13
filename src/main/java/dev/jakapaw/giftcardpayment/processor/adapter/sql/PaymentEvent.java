package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class PaymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = InvoiceEntity.class)
    InvoiceEntity invoiceId;

    String paymentStatus;
    LocalDateTime createdAt;

    public PaymentEvent(InvoiceEntity invoiceId, String paymentStatus, LocalDateTime createdAt) {
        this.invoiceId = invoiceId;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }
}
