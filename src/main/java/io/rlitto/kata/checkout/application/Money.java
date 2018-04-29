package io.rlitto.kata.checkout.application;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class Money {
    public static final int ROUNDING_MODE = BigDecimal.ROUND_DOWN;
    private final Currency currency;
    private final BigDecimal amount;

    public Money(Currency currency, BigDecimal amount) {
        Objects.requireNonNull(currency, "Currency must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        this.currency = currency;
        this.amount = amount.setScale(2, ROUNDING_MODE);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
