package dev.jakapaw.giftcardpayment.processor.adapter.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.processor.application.domain.Invoice;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.stream.Stream;

@Component
public class InvoiceDAO {

    InvoiceRepository invoiceRepository;
    PaymentEventRepository eventRepository;
    ObjectMapper objectMapper;

    public InvoiceDAO(InvoiceRepository invoiceRepository, PaymentEventRepository eventRepository, ObjectMapper objectMapper) {
        this.invoiceRepository = invoiceRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    public void savePayment(Deque<Invoice> eventSeq) {
        Invoice dInvoice = eventSeq.peekLast();

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setId(dInvoice.id());
        invoice.setBuyerId(dInvoice.buyer().id());
        invoice.setPaymentType(dInvoice.buyer().paymentType().name());
        invoice.setSellerId(dInvoice.seller().id());
        invoice.setTotalBill(dInvoice.seller().totalBill());
        invoice.setPaymentStatus(dInvoice.status().name());
        invoice.setCreatedAt(dInvoice.createdAt());

        try {
            String products = objectMapper.writeValueAsString(dInvoice.seller().products());
            invoice.setProducts(products);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        invoiceRepository.save(invoice);

        Stream<PaymentEvent> eventStream = eventSeq.stream()
                .map(el -> new PaymentEvent(invoice, el.status().name(), el.createdAt()));
        eventRepository.saveAllAndFlush(eventStream.toList());
    }
}
