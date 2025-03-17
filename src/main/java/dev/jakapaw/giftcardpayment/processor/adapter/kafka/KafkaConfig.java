package dev.jakapaw.giftcardpayment.processor.adapter.kafka;

import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.PaymentEvent;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaTemplate<String, VerifyGiftcard> verifyGiftcardKafkaTemplate() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.RETRIES_CONFIG, 0);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        ProducerFactory<String, VerifyGiftcard> pf = new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>());
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.RETRIES_CONFIG, 0);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        ProducerFactory<String, PaymentEvent> pf = new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>());
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic giftcardVerify() {
        return new NewTopic("giftcard.verify", 2, (short) 1);
    }

    @Bean
    public NewTopic giftcardVerified() {
        return new NewTopic("giftcard.verified", 2, (short) 1);
    }

    @Bean
    public NewTopic paymentEvent() {
        return new NewTopic("giftcard.payment", 2, (short) 1);
    }
}
