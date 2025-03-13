package dev.jakapaw.giftcardpayment.processor.application.command;

import dev.jakapaw.giftcardpayment.processor.application.domain.Product;

import java.util.List;

public record CreatePaymentCommand(
        String merchantId,
        String giftcardId,
        List<Product> products,
        long totalBill
) { }
