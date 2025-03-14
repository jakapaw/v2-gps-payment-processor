package dev.jakapaw.giftcardpayment.processor.adapter.sql.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class PaymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    InvoiceEntity invoice;

    String paymentStatus;
    LocalDateTime createdAt;

    public PaymentEvent(InvoiceEntity invoice, String paymentStatus, LocalDateTime createdAt) {
        this.invoice = invoice;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }
}
