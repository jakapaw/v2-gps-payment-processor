package dev.jakapaw.giftcardpayment.processor.observability;

import io.micrometer.registry.otlp.OtlpConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

    @Bean
    OtlpConfig otlpConfig() {
        return OtlpConfig.DEFAULT;
    }
}
