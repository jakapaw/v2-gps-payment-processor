package dev.jakapaw.giftcardpayment.processor.application.port.out;

import dev.jakapaw.giftcardpayment.processor.application.command.RetryPaymentCommand;

public interface RetryPayment {

    public void sendRetryNotification(RetryPaymentCommand command);
}
