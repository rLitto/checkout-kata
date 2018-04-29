package io.rlitto.kata.checkout.io

import spock.lang.Subject
import spock.lang.Unroll

class CheckoutSpec extends IOSpec {
    public static final int pA = 10
    public static final int p3A = 25
    public static final int p5A = 40
    public static final int pB = 15
    public static final int p3B = 40
    public static final prices = [UP('A', pA),
                                  UP('B', pB),
                                  MP('A', 3, p3A),
                                  MP('A', 5, p5A),
                                  MP('B', 3, p3B)]

    @Subject
    Checkout sut

    PriceManagement priceManagement

    void setup() {
        sut = application
        priceManagement = application
    }

    def "When I checkout if I do not have items I get a total cost of zero £"() {
        given: "An empty basket"
           Checkout.CheckoutRequest input = new Checkout.CheckoutRequest([])
        when: "I checkout"
           def output = sut.checkout(input)
        then: "I get an amount of zero"
           output?.totalCost?.amount == BigDecimal.ZERO
           output?.totalCost?.currency?.currencyCode == "GBP"
    }

    def "When I checkout if I have items for which there is not price I get an error"() {
        given: "An empty basket"
           Checkout.CheckoutRequest input = new Checkout.CheckoutRequest(['A'])
        when: "I checkout"
           def output = sut.checkout(input)
        then: "I get an amount of zero"
           assert output.error
    }


    @Unroll
    def "When I checkout #items at a Supermarket with set prices (#sPrices) the total is #total p (#comment)"() {
        given: "An basket with #items and #prices"
           priceManagement.setPrices(new PriceManagement.SetPricesRequest(prices.toSet()))
        when: "I checkout"
           def output = sut.checkout(new Checkout.CheckoutRequest(items))
        then: "I get an amount of #total"
           output?.totalCost?.amount == total / 100
           output?.totalCost?.currency?.currencyCode == "GBP"
        where:
           items           | comment
           ['A']           | "If I checkout a priced item the total is equal to its unit price"
           ['A', 'B', 'A'] | "If I checkout multiple priced items and there is no multi-price the total is calculated with the sum of their unit prices"
           ['A', 'B', 'A',
            'A']           | "If I checkout multiple priced items and there is a multi-price, the multi-price has priority over the unit price"
           ['A', 'B', 'A',
            'A', 'A', 'A'] | "If I checkout multiple priced items and there are multiple multi-prices, the multi-prices with the greatest n. of items is applied first"
           ['A', 'B', 'A',
            'A', 'A', 'A',
            'A', 'A', 'A',
            'A']           | "If I checkout multiple priced items and there are multiple multi-prices, the multi-prices are applied starting from the one with the greater n. of items"

           ['A', 'B', 'B'] | "If I checkout multiple priced items and there are only multi-prices with a higher n. of items, the unit prices are applied instead"

           total << [pA, 2 * pA + pB, pB + p3A, pB + p5A, pB + p3A + p5A + pA, 2 * pB + pA]
           sPrices = prices
    }

    @Unroll
    def "When I checkout #items at a Supermarket passing prices (#sPrices) the total is #total p (#comment)"() {
        given: "An basket with #items and a supermarket with no prices set"
           assert priceManagement.retrievePrices().prices.size() == 0
        when: "I checkout passing new prices (#prices)"
           def output = sut.checkoutWithPrices(new Checkout.CheckoutWithPricesRequest(
                   new Checkout.CheckoutRequest(items),
                   sPrices.toSet()
           ))
        then: "I get an amount of #total"
           output?.totalCost?.amount == total / 100
           output?.totalCost?.currency?.currencyCode == "GBP"
        where:
           items           | comment
           ['A']           | "If I checkout a priced item the total is equal to its unit price"
           ['A', 'B', 'A'] | "If I checkout multiple priced items and there is no multi-price the total is calculated with the sum of their unit prices"
           ['A', 'B', 'A',
            'A']           | "If I checkout multiple priced items and there is a multi-price, the multi-price has priority over the unit price"
           ['A', 'B', 'A',
            'A', 'A', 'A'] | "If I checkout multiple priced items and there are multiple multi-prices, the multi-prices with the greatest n. of items is applied first"
           ['A', 'B', 'A',
            'A', 'A', 'A',
            'A', 'A', 'A',
            'A']           | "If I checkout multiple priced items and there are multiple multi-prices, the multi-prices are applied starting from the one with the greater n. of items"

           ['A', 'B', 'B'] | "If I checkout multiple priced items and there are only multi-prices with a higher n. of items, the unit prices are applied instead"

           total << [pA, 2 * pA + pB, pB + p3A, pB + p5A, pB + p3A + p5A + pA, 2 * pB + pA]
           sPrices = prices
    }
}
