package dev.jakapaw.giftcardpayment.processor.adapter.rest;

import dev.jakapaw.giftcardpayment.processor.adapter.rest.model.CreatePaymentDTO;
import dev.jakapaw.giftcardpayment.processor.adapter.rest.model.ProductDTO;
import dev.jakapaw.giftcardpayment.processor.application.command.CreatePaymentCommand;
import dev.jakapaw.giftcardpayment.processor.application.domain.Product;
import dev.jakapaw.giftcardpayment.processor.application.service.GiftcardPaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("giftcard")
public class GiftcardReaderController {

    @Autowired
    GiftcardPaymentProcessor giftcardPaymentProcessor;

    @PostMapping("createpayment")
    public String createPayment(@RequestBody CreatePaymentDTO body) {
        List<Product> productList = new ArrayList<>(body.products().size());
        AtomicLong totalBill = new AtomicLong(0);

        body.products().parallelStream().forEach(el -> {
            Long discount = el.discountAmount();
            if (el.discountAmount() == null) {
                discount = calcDiscount(el);
            }
            Product result = new Product(
                    el.id(),
                    el.name().replaceAll("'", ""),
                    el.price(),
                    el.quantity(),
                    el.qtyUnitName(),
                    el.discountPercent(),
                    discount,
                    calcTax(el)
            );

            productList.add(result);
            totalBill.getAndAdd(result.quantity() * result.price() + result.taxAmount() - result.discountAmount());
        });

        CreatePaymentCommand command = new CreatePaymentCommand(
                body.merchantId(),
                body.giftcardId(),
                productList,
                totalBill.get()
        );
        return giftcardPaymentProcessor.createPayment(command);
    }

    private long calcDiscount(ProductDTO p) {
        if (p.discountAmount() == null) {
            if (p.discountPercent() == null)
                throw new RuntimeException();
            return (p.discountPercent() / 100) * p.price() * p.quantity();
        } else {
            return 0;
        }
    }

    private long calcTax(ProductDTO p) {
        if (p.taxRate() > 0) {
            return p.taxRate() * p.price() * p.quantity() / 100;
        } else {
            return 0;
        }
    }
}
