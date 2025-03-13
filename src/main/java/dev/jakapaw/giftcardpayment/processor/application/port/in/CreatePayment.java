package dev.jakapaw.giftcardpayment.processor.application.port.in;

import dev.jakapaw.giftcardpayment.processor.application.command.CreatePaymentCommand;

public interface CreatePayment {

    public String createPayment(CreatePaymentCommand command);
}
