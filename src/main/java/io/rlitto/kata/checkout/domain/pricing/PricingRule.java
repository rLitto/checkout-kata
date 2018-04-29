package io.rlitto.kata.checkout.domain.pricing;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
interface PricingRule extends Comparable<PricingRule> {

    int LEAST_PRIORITY = Integer.MAX_VALUE;

     void  apply(Basket products);

    @Override
    default int compareTo(@NotNull PricingRule other) {
        return this.priority() - other.priority();
    }

    /**
     * Priority of the Rule
     *
     * @return the priority of the rule
     */
    default int priority() {
        return LEAST_PRIORITY;
    }

    class UnitPricing extends MultiPricing {
        public UnitPricing(String sku, int price) {
            super(sku, 1, price);
        }
    }

    class MultiPricing implements PricingRule {
        final String sku;
        final int price;
        final int nUnits;

        MultiPricing(String sku, int nUnits, int price) {
            Objects.requireNonNull(sku, "SKU must not be null");
            if (price <= 0) throw new IllegalArgumentException("Price must be greater than 0");
            if (nUnits <= 0) throw new IllegalArgumentException("n units must be greater than 0");
            this.sku = sku;
            this.price = price;
            this.nUnits = nUnits;
        }

        @Override
        public int priority() {
            return PricingRule.super.priority() - nUnits;
        }

        @Override
        public void apply(Basket basket) {
            if( basket.items.containsKey(sku)){
                updateBasket(basket);
            }
        }

        void updateBasket(Basket basket) {
            updateTotal(basket);
            updateItems(basket);
        }

        void updateItems(Basket basket) {
            basket.items.computeIfPresent(sku, (key, nItems) -> nItems % nUnits == 0 ? null : nItems % nUnits);
        }

        void updateTotal(Basket basket) {
            final int nCount = basket.items.getOrDefault(sku, 0) /  nUnits;
            basket.total =  basket.total + nCount * price;
        }
    }

}
