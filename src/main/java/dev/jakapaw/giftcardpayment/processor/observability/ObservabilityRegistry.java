package dev.jakapaw.giftcardpayment.processor.observability;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.db.DatabaseTableMetrics;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.metrics.export.otlp.OtlpMetricsExportAutoConfiguration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ObservabilityRegistry {

    private final Map<String, Instant> timerRegistry = new HashMap<>();
    private final MeterRegistry myMeterRegistry;

    @Autowired
    DataSource dataSource;

    @Autowired
    MeterRegistry meterRegistry;

    @Getter
    private static ObservabilityRegistry instance;

    public ObservabilityRegistry(OtlpMetricsExportAutoConfiguration exportAutoConfiguration, OtlpConfig otlpConfig) {
        myMeterRegistry = exportAutoConfiguration.otlpMeterRegistry(otlpConfig, Clock.SYSTEM);
        Metrics.addRegistry(myMeterRegistry);
        instance = this;
    }

    public void addTimer(String key) {
        Instant t0 = Instant.now();
        timerRegistry.put(key, t0);
    }

    public void removeTimer(String key, String timerName, String... tags) {
        Instant t0 = timerRegistry.remove(key);
        if (t0 != null) {
            Instant t1 = Instant.now();
            Timer timer = Timer.builder(timerName)
                    .tags(tags)
                    .register(myMeterRegistry);
            timer.record(Duration.ofMillis(t1.toEpochMilli() - t0.toEpochMilli()));
        }
    }

    public void countInvoice() {
        Counter.builder("payment_completed").register(myMeterRegistry).increment();
    }
}
