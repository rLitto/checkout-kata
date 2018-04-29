package io.rlitto.kata.checkout.domain.pricing;

import io.rlitto.kata.checkout.io.PriceManagement;

import java.util.List;
import java.util.Set;

public interface PricingService {
    int calculateTotalWithPrices(List<String> items, Set<PriceManagement.Price> prices) throws PricingCalculationException;

}
