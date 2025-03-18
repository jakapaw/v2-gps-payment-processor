package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class DatasourceInitialization {

    @Autowired
    DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void loadFunctions() throws IOException {
        ClassPathResource resource = new ClassPathResource("init-pg.sql");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(
                false,
                false,
                StandardCharsets.UTF_8.name(),
                resource
        );
        resourceDatabasePopulator.execute(dataSource);
    }
}
