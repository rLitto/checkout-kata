package io.rlitto.kata.checkout.domain.product;

import java.util.Collection;
import java.util.Set;

public interface ProductRepository {
    void resetProducts(Collection<String> products);

    Set<String> getProducts();
}
