package io.rlitto.kata.checkout.domain.product;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ProductRepositoryMemoryImpl implements ProductRepository {
    private final Set<String> skus = new HashSet<>();

    @Override
    public synchronized void resetProducts(Collection<String> products) {
        skus.clear();
        skus.addAll(products);
    }

    @Override
    public Set<String> getProducts(){
        return new HashSet<>(skus);
    }
}
