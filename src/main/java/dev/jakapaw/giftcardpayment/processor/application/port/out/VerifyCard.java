package dev.jakapaw.giftcardpayment.processor.application.port.out;

import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;

public interface VerifyCard {

    public void verify(VerifyGiftcard data);
}
