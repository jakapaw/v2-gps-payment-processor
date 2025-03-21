package dev.jakapaw.giftcardpayment.processor.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.PublisherGiftcardManager;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.PaymentEvent;
import dev.jakapaw.giftcardpayment.processor.adapter.kafka.model.VerifyGiftcard;
import dev.jakapaw.giftcardpayment.processor.adapter.sql.InvoiceDAO;
import dev.jakapaw.giftcardpayment.processor.application.command.*;
import dev.jakapaw.giftcardpayment.processor.application.domain.*;
import dev.jakapaw.giftcardpayment.processor.application.port.in.CreatePayment;
import dev.jakapaw.giftcardpayment.processor.application.port.in.ProcessPayment;
import dev.jakapaw.giftcardpayment.processor.application.port.out.LogPayment;
import dev.jakapaw.giftcardpayment.processor.application.port.out.RetryPayment;
import dev.jakapaw.giftcardpayment.processor.observability.ObservabilityRegistry;
import io.opentelemetry.context.Context;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class GiftcardPaymentProcessor implements CreatePayment, ProcessPayment, RetryPayment, LogPayment {

    private static final Logger log = LoggerFactory.getLogger(GiftcardPaymentProcessor.class);
    private final InvoiceDAO invoiceDAO;
    private final PublisherGiftcardManager kafkaPublisher;
    private final ObjectMapper om;

    @Getter
    private final Map<String, Deque<Invoice>> invoiceEventsSequence = new ConcurrentHashMap<>();

    public GiftcardPaymentProcessor(InvoiceDAO invoiceDAO, PublisherGiftcardManager kafkaPublisher, ObjectMapper om) {
        this.invoiceDAO = invoiceDAO;
        this.kafkaPublisher = kafkaPublisher;
        this.om = om;
    }

    @Override
    public String createPayment(CreatePaymentCommand command) {
        String invoiceId = Invoice.generateInvoiceId(command.merchantId(), String.valueOf(invoiceDAO.getPostfixSequence()));

        Invoice invoice = new Invoice(
                invoiceId,
                new Buyer(
                        command.giftcardId(),
                        PaymentType.GIFTCARD
                ),
                new Seller(
                        command.merchantId(),
                        command.products(),
                        Seller.calculateBill(command.products())
                ),
                PaymentStatus.CREATED
        );
        Deque<Invoice> events = new LinkedBlockingDeque<>();
        events.add(invoice);
        invoiceEventsSequence.put(invoiceId, events);

        VerifyGiftcard verifyGiftcard = new VerifyGiftcard(
                invoiceId,
                invoice.buyer().id(),
                invoice.seller().totalBill(),
                false,
                null
        );
        kafkaPublisher.verify(verifyGiftcard);
        return invoiceId;
    }

    @Override
    public void checkVerification(VerifyPaymentCommand command) {
        try {
            Deque<Invoice> eventSeq = invoiceEventsSequence.get(command.invoiceId());
            Invoice invoice = invoiceEventsSequence.get(command.invoiceId()).peekLast();

            if (command.isVerified()) {
                Invoice newState = invoice.withStatus(PaymentStatus.VERIFIED);
                eventSeq.add(newState);
                AcceptPaymentCommand acceptPayment = new AcceptPaymentCommand(newState);
                acceptPayment(acceptPayment);
            } else {
                Invoice newState = invoice.withStatus(PaymentStatus.NOTVERIFIED);
                eventSeq.add(newState);
                RetryPaymentCommand retryPayment = new RetryPaymentCommand(newState);
                sendRetryNotification(retryPayment);
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void declinePayment(DeclinePaymentCommand command) {
        Deque<Invoice> eventSeq = invoiceEventsSequence.get(command.invoice().id());
        Invoice declined = command.invoice().withStatus(PaymentStatus.DECLINED);
        eventSeq.add(declined);
        savePayment(eventSeq);
        publishPaymentDeclined(command.invoice());
        invoiceEventsSequence.remove(command.invoice().id());
    }

    @Override
    public void acceptPayment(AcceptPaymentCommand command) {
        Invoice invoice = command.invoice();
        if (invoice == null)
            throw new RuntimeException();

        // In real world, we might send notifications to external systems
        // But for simplicity, we directly change the status to accepted

        Invoice newState = invoice.withStatus(PaymentStatus.ACCEPTED);
        Deque<Invoice> eventSeq = invoiceEventsSequence.get(command.invoice().id());
        eventSeq.add(newState);
        savePayment(eventSeq);
        publishPaymentAccepted(command.invoice());
        invoiceEventsSequence.remove(command.invoice().id());
    }

    @Override
    public void sendRetryNotification(RetryPaymentCommand command) {
        // for now, just decline the payment
        DeclinePaymentCommand decline = new DeclinePaymentCommand(command.invoice());
        declinePayment(decline);
    }

    @Override
    public void savePayment(Deque<Invoice> invoices) {
        try {
            Invoice saved = invoiceDAO.savePayment(invoices);
            if (!invoices.isEmpty()){
                ObservabilityRegistry.getInstance().removeTimer(
                        invoices.peekFirst().buyer().id(), "user_payment_process", "id", saved.id());
            }
        } catch (RuntimeException | JsonProcessingException e) {
            e.printStackTrace();
//            log.error("msg: {}. type: {}", e.getLocalizedMessage(), e.getClass().getSimpleName());
        }
    }

    public void publishPaymentAccepted(Invoice invoice) {
        try {
            String invoiceJson = om.writeValueAsString(invoice);
            kafkaPublisher.publish(new PaymentEvent(invoice.buyer().id(), "PAYMENT_ACCEPTED", invoiceJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishPaymentDeclined(Invoice invoice) {
        try {
            String invoiceJson = om.writeValueAsString(invoice);
            kafkaPublisher.publish(new PaymentEvent(invoice.buyer().id(), "PAYMENT_DECLINED", invoiceJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
