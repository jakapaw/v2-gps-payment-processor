package dev.jakapaw.giftcardpayment.processor.application.domain;

import java.util.Optional;

public record Product(
    String id,
    String name,
    Long price,
    Integer quantity,
    String qtyUnitName,
    Integer discountPercent,
    Long discountAmount,
    Long taxAmount
) {
}
