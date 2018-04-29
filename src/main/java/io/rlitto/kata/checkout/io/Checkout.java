package io.rlitto.kata.checkout.io;

import io.rlitto.kata.checkout.application.Money;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.rlitto.kata.checkout.utils.CollectionUtils.immutableCopyOf;

public interface Checkout {

    class CheckoutWithPricesRequest {
        public final CheckoutRequest checkoutRequest;
        public final Set<PriceManagement.Price> prices;

        public CheckoutWithPricesRequest(CheckoutRequest checkoutRequest, Set<PriceManagement.Price> prices) {
            this.checkoutRequest = checkoutRequest;
            this.prices = prices;
        }
    }

    class CheckoutRequest {
        public final List<String> items;

        public CheckoutRequest(@NotNull List<String> items) {
            Objects.requireNonNull(items, "Items cannot be null");
            this.items = immutableCopyOf(items);
        }

    }

    class CheckoutResponse extends Response {
        private final Money totalCost;

        private CheckoutResponse(Money totalCost, String errorMessage) {
            super(errorMessage);
            this.totalCost = totalCost;
        }

        public static CheckoutResponse success(Money totalCost){
            Objects.requireNonNull(totalCost, "Total cost cannot be null for a successful checkout");
            return new CheckoutResponse(totalCost, null);
        }

        public static CheckoutResponse error(String errorMessage){
            Objects.requireNonNull(errorMessage, "Error message cannot be null for a failed checkout");
            return new CheckoutResponse(null, errorMessage);
        }

        public Money getTotalCost() {
            return totalCost;
        }


    }

    CheckoutResponse checkout(CheckoutRequest input);
    CheckoutResponse checkoutWithPrices(CheckoutWithPricesRequest input);
}
