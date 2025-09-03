package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import java.util.List;

public class ListProductsUseCase {
    private final ProductRepositoryPort repo;

    public ListProductsUseCase(ProductRepositoryPort repo) {
        this.repo = repo;
    }

    public List<Product> execute() {
        return repo.findAll();
    }
}
