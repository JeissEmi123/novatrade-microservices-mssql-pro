package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;

public class CreateProductUseCase {
    private final ProductRepositoryPort repo;

    public CreateProductUseCase(ProductRepositoryPort repo) {
        this.repo = repo;
    }

    public Product execute(Product p) {
        validateProduct(p);
        return repo.save(p);
    }

    private void validateProduct(Product p) {
        validateName(p);
        validatePrice(p);
    }

    private void validateName(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }

    private void validatePrice(Product p) {
        if (p.getPrice() == null || p.getPrice() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
    }
}
