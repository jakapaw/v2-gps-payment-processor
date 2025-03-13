package dev.jakapaw.giftcardpayment.processor.application.domain;

import java.util.List;

public record Seller(
        String id,
        List<Product> products,
        long totalBill
) {

    public static long calculateBill(List<Product> products) {
        long sum = 0;
        for (var product : products) {
            sum += product.quantity() * product.price();
        }
        return sum;
    }
}