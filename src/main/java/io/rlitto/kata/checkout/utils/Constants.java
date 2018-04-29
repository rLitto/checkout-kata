package io.rlitto.kata.checkout.utils;

import java.util.Currency;

public final class Constants {
    public static final Currency CURRENCY = Currency.getInstance("GBP");

    private Constants(){
        //prevent modifications, avoid interface antipattern
    }


}
