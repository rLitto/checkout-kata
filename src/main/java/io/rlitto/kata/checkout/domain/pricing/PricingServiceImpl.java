package io.rlitto.kata.checkout.domain.pricing;

import io.rlitto.kata.checkout.io.PriceManagement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PricingServiceImpl implements PricingService {


    private final PriceMapper mapper;

    public PricingServiceImpl(PriceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int calculateTotalWithPrices(List<String> items, Set<PriceManagement.Price> prices) throws PricingCalculationException {
        Basket basket = createBasket(items);
        List<PricingRule> rules = createRules(prices);
        rules.forEach(rule -> rule.apply(basket));
        if(basket.items.size() > 0) {
            throw new PricingCalculationException("Calculation error, some items were not priced", basket.items);
        }
        return basket.total;
    }

    private List<PricingRule> createRules(Collection<PriceManagement.Price> prices) {
        return prices.stream().map(this::createRule).sorted().collect(Collectors.toList());
    }

    private PricingRule createRule(PriceManagement.Price price) {
        if (price == null) throw new IllegalArgumentException("Null price");
        return mapper.mapToRule(price);
    }

    private Basket createBasket(List<String> items) {
        return new Basket(collectItems(items));
    }

    private Map<String, Integer> collectItems(List<String> items) {
        return items.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.reducing(0, e -> 1, Integer::sum)));
    }
}
