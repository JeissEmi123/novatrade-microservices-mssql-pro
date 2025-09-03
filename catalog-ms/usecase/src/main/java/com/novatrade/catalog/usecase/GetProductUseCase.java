package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import java.util.NoSuchElementException;

public class GetProductUseCase {
    private final ProductRepositoryPort repo;

    public GetProductUseCase(ProductRepositoryPort repo) {
        this.repo = repo;
    }

    public Product execute(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("product not found"));
    }
}
