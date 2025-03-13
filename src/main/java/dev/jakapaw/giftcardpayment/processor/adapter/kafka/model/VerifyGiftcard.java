package dev.jakapaw.giftcardpayment.processor.adapter.kafka.model;

public record VerifyGiftcard(
        String invoiceId,
        String cardId,
        Long totalBilled,
        Boolean isVerified,
        String message
) { }
