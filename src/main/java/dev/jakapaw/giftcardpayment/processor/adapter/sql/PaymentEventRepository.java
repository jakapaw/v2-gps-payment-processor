package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import dev.jakapaw.giftcardpayment.processor.adapter.sql.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, String> {
}
