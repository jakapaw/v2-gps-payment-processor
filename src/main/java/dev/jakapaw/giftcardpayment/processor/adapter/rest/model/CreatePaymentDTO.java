package dev.jakapaw.giftcardpayment.processor.adapter.rest.model;

import java.util.List;

public record CreatePaymentDTO(
        String merchantId,
        String giftcardId,
        List<ProductDTO> products
) {
}
