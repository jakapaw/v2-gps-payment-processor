package dev.jakapaw.giftcardpayment.processor.application.command;

import dev.jakapaw.giftcardpayment.processor.application.domain.Invoice;

public record AcceptPaymentCommand (
        Invoice invoice
) {
}
