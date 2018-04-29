package io.rlitto.kata.checkout.domain.pricing;

import java.util.Map;

import static io.rlitto.kata.checkout.utils.CollectionUtils.immutableCopyOf;

public class PricingCalculationException extends Exception {

    private final Map<String, Integer> items;

    public PricingCalculationException(String message, Map<String, Integer> items) {
        super(message);
        this.items = immutableCopyOf(items);
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
