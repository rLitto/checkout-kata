package io.rlitto.kata.checkout.io


import spock.lang.Subject

import static io.rlitto.kata.checkout.io.PriceManagement.SetPricesRequest

class PriceManagementSpec extends IOSpec {
    @Subject
    PriceManagement sut


    void setup() {
        sut = application
    }

    def "when I set the prices then the same prices are returned"() {
        given: "no prices are set"
        when:
           Set prices = [UP('A', 12), UP('B', 32)]
           def request = new SetPricesRequest(prices)
           sut.setPrices(request)
        then:
           getPrices() == prices
    }

    def "when I set the prices and I have multi prices and I don't have the equivalent unit price I get an error and no price is set"() {
        given: "no prices are set"
        when:
           Set prices = [MP('A', 2, 20), UP('B', 32)]
           def request = new SetPricesRequest(prices)
           def result = sut.setPrices(request)
        then:
           assert result.error
           getPrices().size() == 0
    }

    def "when I set the prices and I have multiple unit prices for the same sku I get an error and no price is set"() {
        given: "no prices are set"
        when:
           Set prices = [UP('A', 20), UP('A', 32)]
           def request = new SetPricesRequest(prices)
           def result = sut.setPrices(request)
        then:
           assert result.error
           getPrices().size() == 0
    }

    def "when I set the prices and I have multiple multi prices for the same sku and amount I get an error and no price is set"() {
        given: "no prices are set"
        when:
           Set prices = [
                   UP('A', 20),
                   MP('A', 2, 32),
                   MP('A', 2, 35)
           ]
           def request = new SetPricesRequest(prices)
           def result = sut.setPrices(request)
        then:
           assert result.error
           getPrices().size() == 0
    }

    def "when I set the prices and I have multiple multi prices for the same sku and different amount I get a success and the values are set"() {
        given: "no prices are set"
        when:
           Set prices = [UP('A', 20),
                         MP('A', 2, 32),
                         MP('A', 3, 50)]
           def request = new SetPricesRequest(prices)
           def result = sut.setPrices(request)
        then:
           assert !result.error
           getPrices().size() == 3
    }

    Set<PriceManagement.Price> getPrices() {
        return sut.retrievePrices().prices
    }

    def "when I get prices and none are set I am returned an empty list"() {
        given: "no prices are set"
        when:
           def prices = getPrices()
        then:
           prices.size() == 0
    }
}
