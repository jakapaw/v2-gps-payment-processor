package dev.jakapaw.giftcardpayment.processor.adapter.kafka;

import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.PaymentEvent;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import dev.jakapaw.giftcardpayment.processor.application.port.out.PublishPaymentEvent;
import dev.jakapaw.giftcardpayment.processor.application.port.out.VerifyCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PublisherGiftcardManager implements VerifyCard, PublishPaymentEvent {

    @Autowired
    KafkaTemplate<String, VerifyGiftcard> verifyGiftcardKafkaTemplate;

    @Autowired
    KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;

    @Autowired
    KafkaConfig kafkaConfig;

    @Override
    public void verify(VerifyGiftcard data) {
        verifyGiftcardKafkaTemplate.send(kafkaConfig.giftcardVerify().name(), data.cardId(), data);
    }

    @Override
    public void publish(PaymentEvent event) {
        paymentEventKafkaTemplate.send(kafkaConfig.paymentEvent().name(), event.cardId(), event);
    }
}
