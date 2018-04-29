package io.rlitto.kata.checkout.domain.pricing;

import io.rlitto.kata.checkout.io.PriceManagement;


interface PriceMapper {

    PricingRule mapToRule(PriceManagement.Price price);

    interface Converter<S extends PriceManagement.Price, T extends PricingRule> {

        T convert(S source);

        default boolean canApply(PriceManagement.Price source) {
            return true;
        }
    }
}
