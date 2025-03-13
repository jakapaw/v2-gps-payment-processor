package dev.jakapaw.giftcardpayment.processor.application.command;

public record VerifyPaymentCommand(
        String invoiceId,
        String cardId,
        Long totalBilled,
        Boolean isVerified
) { }
