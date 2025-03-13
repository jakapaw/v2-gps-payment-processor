package dev.jakapaw.giftcardpayment.processor.application.port.out;

import dev.jakapaw.giftcardpayment.processor.application.domain.Invoice;

import java.util.Deque;

public interface LogPayment {

    public void savePayment(Deque<Invoice> invoices);
}
