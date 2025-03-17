package dev.jakapaw.giftcardpayment.processor.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import dev.jakapaw.giftcardpayment.processor.application.command.VerifyPaymentCommand;
import dev.jakapaw.giftcardpayment.processor.application.service.GiftcardPaymentProcessor;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ConsumerGiftcardManager {

    GiftcardPaymentProcessor giftcardPaymentProcessor;
    ObjectMapper om;

    public ConsumerGiftcardManager(GiftcardPaymentProcessor giftcardPaymentProcessor, ObjectMapper om) {
        this.giftcardPaymentProcessor = giftcardPaymentProcessor;
        this.om = om;
    }

    @KafkaListener(topics = "giftcard.verified", groupId = "verification")
    public void listenVerifyGiftcard(String payload, @Headers Map<String, byte[]> headers) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        KafkaHeaderGetter kafkaHeaderGetter = new KafkaHeaderGetter();

        Context extractedContext = contextPropagators.getTextMapPropagator()
                .extract(Context.current(), headers, kafkaHeaderGetter);

        try (Scope scope = extractedContext.makeCurrent()) {
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

    private static class KafkaHeaderGetter implements TextMapGetter<Map<String, byte[]>> {

        @Override
        public Iterable<String> keys(Map<String, byte[]> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, byte[]> carrier, String key) {
            if (carrier == null) {
                throw new IllegalArgumentException("Carrier must not be null");
            }
            if (carrier.containsKey(key))
                return new String(carrier.get(key), StandardCharsets.UTF_8);
            else
                return null;
        }
    }
}
