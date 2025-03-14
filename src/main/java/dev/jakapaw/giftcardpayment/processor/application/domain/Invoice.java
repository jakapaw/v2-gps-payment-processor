package dev.jakapaw.giftcardpayment.processor.application.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.With;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.lang.NonNull;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.Random;
import java.util.SplittableRandom;

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
        RandomStringUtils randomString = RandomStringUtils.secure();
        LocalDate date = LocalDate.now();
        return sellerId + date.getDayOfMonth() + date.getMonthValue() + date.getYear()
                + randomString.nextAlphabetic(2).toUpperCase() + randomString.nextNumeric(5);
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