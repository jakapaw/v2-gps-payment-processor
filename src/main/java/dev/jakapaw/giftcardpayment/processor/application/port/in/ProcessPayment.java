package dev.jakapaw.giftcardpayment.processor.application.port.in;

import dev.jakapaw.giftcardpayment.processor.application.command.AcceptPaymentCommand;
import dev.jakapaw.giftcardpayment.processor.application.command.DeclinePaymentCommand;
import dev.jakapaw.giftcardpayment.processor.application.command.VerifyPaymentCommand;

public interface ProcessPayment {

    public void acceptPayment(AcceptPaymentCommand command);

    public void checkVerification(VerifyPaymentCommand command);

    public void declinePayment(DeclinePaymentCommand command);
}
