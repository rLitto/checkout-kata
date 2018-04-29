package io.rlitto.kata.checkout.io;

import io.rlitto.kata.checkout.utils.Constants;

import java.util.Objects;
import java.util.Set;

import static io.rlitto.kata.checkout.utils.CollectionUtils.immutableCopyOf;

public interface PriceManagement {
    GetPricesResponse retrievePrices();

    Response setPrices(SetPricesRequest input);

    class SetPricesRequest {
        public final Set<Price> prices;

        public SetPricesRequest(Set<Price> prices) {
            Objects.requireNonNull(prices, "Prices cannot be null");
            this.prices = immutableCopyOf(prices);
        }
    }

    class GetPricesResponse extends Response {
        public final Set<Price> prices;

        public GetPricesResponse(Set<Price> prices) {
            super(null);
            Objects.requireNonNull(prices, "Prices cannot be null");
            this.prices = immutableCopyOf(prices);
        }
    }


    class MultiPrice extends Price {
        public final String sku;
        public final int nUnits;


        public MultiPrice(String sku, int nUnits, int amountInPence) {
            super(amountInPence);
            Objects.requireNonNull(sku, "SKU must not be null");
            if (nUnits <= 1) throw new IllegalArgumentException("n units must be greater than one");
            this.sku = sku;
            this.nUnits = nUnits;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MultiPrice that = (MultiPrice) o;
            return nUnits == that.nUnits &&
                    Objects.equals(sku, that.sku);
        }

        @Override
        public int hashCode() {

            return Objects.hash(super.hashCode(), sku, nUnits);
        }

        @Override
        public String toString() {
            return String.format("%d x %s: %s %.2f", nUnits, sku, Constants.CURRENCY.getSymbol(), amountInPence/100.0);
        }
    }

    abstract class Price {
        public final int amountInPence;

        public Price(int amountInPence) {
            if (amountInPence <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
            this.amountInPence = amountInPence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Price price = (Price) o;
            return amountInPence == price.amountInPence;
        }

        @Override
        public int hashCode() {
            return Objects.hash(amountInPence);
        }
    }

    class UnitPrice extends Price {
        public final String sku;

        public UnitPrice(String sku, int amountInPence) {
            super(amountInPence);
            Objects.requireNonNull(sku, "SKU must not be null");
            this.sku = sku;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UnitPrice unitPrice = (UnitPrice) o;
            return Objects.equals(sku, unitPrice.sku);
        }

        @Override
        public int hashCode() {

            return Objects.hash(super.hashCode(), sku);
        }

        @Override
        public String toString() {
            return String.format("%s: %s %.2f", sku, Constants.CURRENCY.getSymbol(), amountInPence/100.0);
        }
    }
}
