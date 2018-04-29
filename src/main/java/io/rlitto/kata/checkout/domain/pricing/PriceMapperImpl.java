package io.rlitto.kata.checkout.domain.pricing;

import io.rlitto.kata.checkout.io.PriceManagement;

import java.util.ArrayList;
import java.util.List;

import static io.rlitto.kata.checkout.domain.pricing.PricingRule.MultiPricing;
import static io.rlitto.kata.checkout.domain.pricing.PricingRule.UnitPricing;

public class PriceMapperImpl implements PriceMapper {

    private final static List<Converter<? extends PriceManagement.Price, ? extends PricingRule>> converters = new ArrayList<>();
    private final static Converter<PriceManagement.Price, PricingRule> FALLBACK =
            source -> {
                throw new UnsupportedOperationException("Unable to convert class: " + source.getClass().getName());
            };

    private static final Converter<PriceManagement.UnitPrice, UnitPricing> UNIT_PRICE_CONVERTER = new Converter<PriceManagement.UnitPrice, UnitPricing>() {
        @Override
        public UnitPricing convert(PriceManagement.UnitPrice source) {
            return source == null ? null : new UnitPricing(source.sku, source.amountInPence);
        }

        @Override
        public boolean canApply(PriceManagement.Price source) {
            return source instanceof PriceManagement.UnitPrice;
        }
    };

    private static final Converter<PriceManagement.MultiPrice, MultiPricing> MULTI_PRICE_CONVERTER = new Converter<PriceManagement.MultiPrice, MultiPricing>() {
        @Override
        public MultiPricing convert(PriceManagement.MultiPrice source) {
            return source == null ? null : new MultiPricing(source.sku, source.nUnits, source.amountInPence);
        }

        @Override
        public boolean canApply(PriceManagement.Price source) {
            return source instanceof PriceManagement.MultiPrice;
        }
    };

    // with frameworks, libraries like guava or java9 this would be cleaner
    static {
        converters.add(UNIT_PRICE_CONVERTER);
        converters.add(MULTI_PRICE_CONVERTER);
    }

    public PricingRule mapToRule(PriceManagement.Price price) {
        return getConverter(price)
                .convert(price);
    }

    @SuppressWarnings("unchecked")
    private Converter<PriceManagement.Price, PricingRule> getConverter(PriceManagement.Price price) {
        return (Converter<PriceManagement.Price, PricingRule>) converters.stream()
                .filter(converter -> converter.canApply(price))
                .findFirst()
                .orElse(FALLBACK);
    }
}
