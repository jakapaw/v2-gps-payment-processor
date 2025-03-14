package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import dev.jakapaw.giftcardpayment.processor.adapter.sql.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {

}
