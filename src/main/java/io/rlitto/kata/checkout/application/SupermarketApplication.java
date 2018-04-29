package io.rlitto.kata.checkout.application;

import io.rlitto.kata.checkout.domain.pricing.PricingCalculationException;
import io.rlitto.kata.checkout.domain.pricing.PricingService;
import io.rlitto.kata.checkout.domain.product.ProductRepository;
import io.rlitto.kata.checkout.io.Checkout;
import io.rlitto.kata.checkout.io.PriceManagement;
import io.rlitto.kata.checkout.io.Response;
import io.rlitto.kata.checkout.utils.CollectionUtils;
import io.rlitto.kata.checkout.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SupermarketApplication implements Checkout, PriceManagement {

    private final ProductRepository productRepository;
    private final PricingService priceCalculatorService;
    private Set<Price> prices = Collections.emptySet();

    public SupermarketApplication(ProductRepository productRepository, PricingService priceCalculatorService) {
        this.productRepository = productRepository;
        this.priceCalculatorService = priceCalculatorService;
    }

    private static boolean hasMultipleOccurrences(Map<String, Long> skuMap) {
        return skuMap.entrySet().stream().anyMatch(e -> e.getValue() > 1L);
    }

    @Override
    public CheckoutResponse checkout(CheckoutRequest input) {
        final Set<Price> prices = this.prices;
        return checkoutItemsWithPrices(input.items, prices);
    }

    @NotNull
    private CheckoutResponse checkoutItemsWithPrices(List<String> items, Set<Price> prices) {
        final Set<String> products = extractProductsFromUnitPrices(prices);
        final Optional<String> errorMessage = validateProducts(items, products);
        return errorMessage
                .map(CheckoutResponse::error)
                .orElseGet(() -> calculateCheckoutTotal(items, prices));
    }

    @Override
    public CheckoutResponse checkoutWithPrices(CheckoutWithPricesRequest input) {
        return checkoutItemsWithPrices(input.checkoutRequest.items, input.prices);
    }

    @NotNull
    private CheckoutResponse calculateCheckoutTotal(List<String> items, Set<Price> prices) {
        try {
            int total = priceCalculatorService.calculateTotalWithPrices(items, prices);
            return CheckoutResponse.success(getMoney(total));
        } catch (PricingCalculationException ex) {
            return CheckoutResponse.error(getErrorMessage(ex));
        }
    }

    @NotNull
    private Money getMoney(int total) {
        return new Money(Constants.CURRENCY, new BigDecimal(total).movePointLeft(2));
    }

    @NotNull
    private String getErrorMessage(PricingCalculationException ex) {
        return ex.getMessage() + ex.getItems().entrySet().stream().map(e -> e.getValue() + " * " + e.getKey()).collect(Collectors.joining("\n"));
    }

    private Set<String> extractProductsFromUnitPrices(Set<Price> prices) {
        return prices.stream().filter(p -> p instanceof UnitPrice).map(p -> (UnitPrice) p).map(p -> p.sku).collect(Collectors.toSet());
    }

    @Override
    public GetPricesResponse retrievePrices() {
        return new GetPricesResponse(prices);
    }

    @Override
    public Response setPrices(SetPricesRequest input) {
        final Set<Price> prices = input.prices;
        final Optional<String> error = validatePrices(prices);
        if (error.isPresent()) {
            return Response.error(error.get());
        }
        resetProducts(prices);
        resetPrices(prices);
        return Response.success();
    }

    private void resetProducts(Set<Price> prices) {
        final Set<String> products = extractProductsFromUnitPrices(prices);
        productRepository.resetProducts(products);
    }

    private void resetPrices(Set<Price> prices) {
        this.prices = CollectionUtils.immutableCopyOf(prices);
    }

    private Optional<String> validatePrices(Set<Price> prices) {
        StringBuilder message = new StringBuilder();
        final Map<String, Long> unitPricesSkuCount = groupUnitBySkuAndCount(prices);
        if (hasMultipleOccurrences(unitPricesSkuCount)) {
            message.append("Multiple unit price for the same product");
        }

        final Map<String, Long> multiPriceSkuCount = groupMultiBySkuAndNAndCount(prices);
        if (hasMultipleOccurrences(multiPriceSkuCount)) {
            message.append("Multiple multi price for the same product and amount");
        }
        final Set<String> products = unitPricesSkuCount.keySet();
        final Set<String> multiSku = prices.stream().filter(p -> p instanceof MultiPrice).map(p -> (MultiPrice) p).map(p -> p.sku).collect(Collectors.toSet());
        if (!products.containsAll(multiSku)) {
            message.append("MultiPrice not matching any product");
        }
        return message.length() == 0 ? Optional.empty() : Optional.of(message.toString());
    }

    private Optional<String> validateProducts(Collection<String> items, Collection<String> products) {
        final String message = items.stream()
                .distinct()
                .filter(s -> !products.contains(s))
                .map(s -> "Missing product: " + s)
                .collect(Collectors.joining("\n"));
        return message.isEmpty() ? Optional.empty() : Optional.of(message);
    }

    private Map<String, Long> groupMultiBySkuAndNAndCount(Set<Price> prices) {
        return prices.stream().filter(p -> p instanceof MultiPrice).map(p -> (MultiPrice) p).collect(Collectors.groupingBy(p -> "" + p.nUnits + p.sku, Collectors.counting()));
    }

    private Map<String, Long> groupUnitBySkuAndCount(Set<Price> prices) {
        return prices.stream().filter(p -> p instanceof UnitPrice).map(p -> (UnitPrice) p).collect(Collectors.groupingBy(p -> p.sku, Collectors.counting()));
    }
}
