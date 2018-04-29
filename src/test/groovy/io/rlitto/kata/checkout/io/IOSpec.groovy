package io.rlitto.kata.checkout.io

import io.rlitto.kata.checkout.application.SupermarketApplication

import io.rlitto.kata.checkout.domain.pricing.PricingServiceImpl
import io.rlitto.kata.checkout.domain.pricing.PriceMapperImpl
import io.rlitto.kata.checkout.domain.product.ProductRepositoryMemoryImpl
import spock.lang.Specification

class IOSpec extends Specification {
    SupermarketApplication application

    static def UP(String sku, int amount) {
        return new PriceManagement.UnitPrice(sku, amount)
    }

    static def MP(String sku, int n, int amount) {
        return new PriceManagement.MultiPrice(sku, n, amount)
    }

    void setup() {
        def mapper = new PriceMapperImpl();
        def productService = new ProductRepositoryMemoryImpl()
        def priceCalculatorService = new PricingServiceImpl(mapper)
        application = new SupermarketApplication(productService, priceCalculatorService)
    }
}