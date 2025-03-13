package dev.jakapaw.giftcardpayment.processor.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import dev.jakapaw.giftcardpayment.processor.application.command.VerifyPaymentCommand;
import dev.jakapaw.giftcardpayment.processor.application.service.GiftcardPaymentProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerGiftcardManager {

    GiftcardPaymentProcessor giftcardPaymentProcessor;
    ObjectMapper om;

    public ConsumerGiftcardManager(GiftcardPaymentProcessor giftcardPaymentProcessor, ObjectMapper om) {
        this.giftcardPaymentProcessor = giftcardPaymentProcessor;
        this.om = om;
    }

    @KafkaListener(topics = "giftcard.verified", groupId = "verification")
    public void listenVerifyGiftcard(String payload) {
        try {
            VerifyGiftcard message = om.readValue(payload, VerifyGiftcard.class);
            VerifyPaymentCommand command = new VerifyPaymentCommand(
                    message.invoiceId(),
                    message.cardId(),
                    message.totalBilled(),
                    message.isVerified()
            );
            giftcardPaymentProcessor.checkVerification(command);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
