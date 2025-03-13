package dev.jakapaw.giftcardpayment.processor.application.domain;

import java.util.Optional;

public record Buyer(
        String id,
        PaymentType paymentType
) { }
