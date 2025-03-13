package dev.jakapaw.giftcardpayment.processor.adapter.kafka.model;

public record PaymentEvent(
        String cardId,
        String eventName,
        String eventData
) { }
