package dev.jakapaw.giftcardpayment.processor.adapter.rest.model;

import java.util.Optional;

public record ProductDTO(
    String id,
    String name,
    Long price,
    Integer quantity,
    String qtyUnitName,
    Integer discountPercent,
    Long discountAmount,
    Integer taxRate
) {
}
