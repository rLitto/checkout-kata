package io.rlitto.kata.checkout.domain.pricing;

import java.util.HashMap;
import java.util.Map;

class Basket {
    final Map<String, Integer> items;
    int total;

    Basket(Map<String, Integer> items) {
        this(items, 0);
    }

    Basket(Map<String, Integer> items, int total) {
        this.items = new HashMap<>(items);
        this.total = total;
    }
}
