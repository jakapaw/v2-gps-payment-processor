package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.processor.adapter.sql.entity.InvoiceEntity;
import dev.jakapaw.giftcardpayment.processor.adapter.sql.entity.PaymentEvent;
import dev.jakapaw.giftcardpayment.processor.application.domain.Invoice;
import dev.jakapaw.giftcardpayment.processor.observability.ObservabilityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.stream.Stream;

@Component
public class InvoiceDAO {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDAO.class);
    InvoiceRepository invoiceRepository;
    PaymentEventRepository eventRepository;
    ObjectMapper objectMapper;
    JdbcTemplate jdbcTemplate;

    public InvoiceDAO(InvoiceRepository invoiceRepository, PaymentEventRepository eventRepository, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Invoice savePayment(Deque<Invoice> eventSeq) throws JsonProcessingException {
        Invoice dInvoice = eventSeq.peekLast();

        InvoiceEntity einvoice = new InvoiceEntity();
        einvoice.setId(dInvoice.id());
        einvoice.setBuyerId(dInvoice.buyer().id());
        einvoice.setPaymentType(dInvoice.buyer().paymentType().name());
        einvoice.setSellerId(dInvoice.seller().id());
        einvoice.setTotalBill(dInvoice.seller().totalBill());
        einvoice.setPaymentStatus(dInvoice.status().name());
        einvoice.setCreatedAt(dInvoice.createdAt());

        String products = objectMapper.writeValueAsString(dInvoice.seller().products());
        einvoice.setProducts("E" + products);           // 'E' to tell postgres this string use C like escape string

        invoiceRepository.save(einvoice);

        Stream<PaymentEvent> eventStream = eventSeq.stream()
                .map(el -> new PaymentEvent(einvoice, el.status().name(), el.createdAt()));
        eventRepository.saveAllAndFlush(eventStream.toList());

        ObservabilityRegistry.getInstance().countInvoice();

        return dInvoice;
    }

    public boolean isInvoiceExist(String invoiceId) {
        return invoiceRepository.existsById(invoiceId);
    }

    public long getPostfixSequence() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT nextval('invoice_postfix')");
        rowSet.next();
        return rowSet.getLong(1);
    }
}
