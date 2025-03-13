package dev.jakapaw.giftcardpayment.processor.application.port.out;

import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.PaymentEvent;

public interface PublishPaymentEvent {

    public void publish(PaymentEvent event);
}
