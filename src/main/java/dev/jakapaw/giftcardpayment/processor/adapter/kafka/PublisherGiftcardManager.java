package dev.jakapaw.giftcardpayment.processor.adapter.kafka;

import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.PaymentEvent;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import dev.jakapaw.giftcardpayment.processor.application.port.out.PublishPaymentEvent;
import dev.jakapaw.giftcardpayment.processor.application.port.out.VerifyCard;
import io.micrometer.observation.Observation;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PublisherGiftcardManager implements VerifyCard, PublishPaymentEvent {

    private static final Logger log = LoggerFactory.getLogger(PublisherGiftcardManager.class);
    @Autowired
    KafkaTemplate<String, VerifyGiftcard> verifyGiftcardKafkaTemplate;

    @Autowired
    KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;

    @Autowired
    KafkaConfig kafkaConfig;

    @Override
    public void verify(VerifyGiftcard event) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        TextMapSetter<ProducerRecord<String, VerifyGiftcard>> textMapSetter = new KafkaHeaderSetter<>();

        ProducerRecord<String, VerifyGiftcard> record =
                new ProducerRecord<>(kafkaConfig.giftcardVerify().name(), event.cardId(), event);

        // inject context into kafka ProducerRecord
        contextPropagators.getTextMapPropagator().inject(Context.current(), record, textMapSetter);

        verifyGiftcardKafkaTemplate.send(record);
    }

    @Override
    public void publish(PaymentEvent event) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        TextMapSetter<ProducerRecord<String, PaymentEvent>> textMapSetter = new KafkaHeaderSetter<>();

        ProducerRecord<String, PaymentEvent> record =
                new ProducerRecord<>(kafkaConfig.paymentEvent().name(), event.cardId(), event);

        // inject context into kafka ProducerRecord
        contextPropagators.getTextMapPropagator().inject(Context.current(), record, textMapSetter);
        paymentEventKafkaTemplate.send(record);
    }

    private static class KafkaHeaderSetter<K, V> implements TextMapSetter<ProducerRecord<K, V>> {
        @Override
        public void set(ProducerRecord<K, V> carrier, String key, String val) {
            if (carrier == null) {
                throw new IllegalArgumentException("Carrier must not be null");
            }
            carrier.headers().add(key, val.getBytes(StandardCharsets.UTF_8));
        }
    }
}