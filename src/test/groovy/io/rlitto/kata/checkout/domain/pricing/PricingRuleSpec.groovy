package io.rlitto.kata.checkout.domain.pricing

import spock.lang.Specification
import spock.lang.Unroll


class PricingRuleSpec extends Specification {

    @Unroll
    def "A multi price for #nUnits1 units has #compare priority of a multi price for #nUnits2 units"() {
        def sku = "A"
        when:
           PricingRule rule1 = new PricingRule.MultiPricing(sku, nUnits1, 20)
           PricingRule rule2 = new PricingRule.MultiPricing(sku, nUnits2, 20)
        then:
           rule1.compareTo(rule2) == result
           rule1.priority() - rule2.priority() == result
        where:
           nUnits1 | nUnits2 | compare   | result
           1       | 1       | "same"    | 0
           1       | 2       | "greater" | 1
           2       | 2       | "same"    | 0
           3       | 2       | "lesser"  | -1

    }

    @Unroll
    def "if we apply a unit price of #price to a basket with #n item(s) and total = #total, the new basket is empty and with a total to #newTotal"() {
        def sku = "A"
        given:
           PricingRule rule = new PricingRule.UnitPricing(sku, price)
           Basket basket = new Basket([(sku): n], total)
        when:
           rule.apply(basket)
        then:
           basket.total == newTotal
           assert basket.items.isEmpty()
        where:
           n | price | total
           1 | 20    | 0
           1 | 20    | 5
           1 | 40    | 5
           2 | 20    | 0
           2 | 20    | 15
           2 | 40    | 15
           3 | 25    | 20

           newTotal = total + n * price

    }


    @Unroll
    def "if we apply a multi price of #price per #nUnits to a basket with #nItems item(s) and total = #total, the new basket has a new size of #newSize and a total of #newTotal"() {
        def sku = "A"
        given:
           PricingRule rule = new PricingRule.MultiPricing(sku, nUnits, price)
           Basket basket = new Basket([(sku): nItems], total)
        when:
           rule.apply(basket)
        then:
           basket.total == newTotal
           basket.items.size() == newSize
        where:
           nItems | nUnits | price | total
           1      | 2      | 20    | 0
           2      | 2      | 20    | 0
           2      | 2      | 40    | 0
           2      | 2      | 20    | 5
           3      | 2      | 20    | 0
           3      | 2      | 20    | 5
           6      | 3      | 40    | 15
           7      | 2      | 25    | 0

           newTotal = total + (nItems.intdiv(nUnits)) * (int) price
           newSize = nItems % nUnits

    }

    @Unroll
    def "if we apply a unit price for an item to a basket with no items of that type, the basket is unchanged"() {
        given:
           PricingRule rule = new PricingRule.UnitPricing("A", price)
           def startingItems = ["B": 1]
           Basket basket = new Basket(startingItems, 10)
           def startingTotal = basket.total
        when:
           rule.apply(basket)
        then:
           startingItems == basket.items
           startingTotal == basket.total
        where:
           price << [20, 40, 15]
    }
}
