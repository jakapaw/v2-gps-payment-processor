package dev.jakapaw.giftcardpayment.processor.application.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.With;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Random;

public record Invoice(
        String id,
        Buyer buyer,
        Seller seller,
        PaymentStatus status,
        LocalDateTime createdAt
) {

    public Invoice(String id, Buyer buyer, Seller seller, PaymentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.buyer = buyer;
        this.seller = seller;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Invoice(String id, Buyer buyer, Seller seller, PaymentStatus status) {
        this(id, buyer, seller, status, LocalDateTime.now());
    }

    public static String generateInvoiceId(String sellerId) {
        Random random = new Random();
        long postfix = random.nextLong(10000, 100000);
        return sellerId + postfix;
    }

    public Invoice withStatus(PaymentStatus status) {
        return this.status == status ? this : new Invoice(
                this.id,
                this.buyer,
                this.seller,
                status
        );
    }
}