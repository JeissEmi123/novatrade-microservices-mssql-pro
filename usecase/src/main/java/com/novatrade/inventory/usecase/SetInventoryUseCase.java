package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;

public class SetInventoryUseCase {
    private final InventoryRepositoryPort repo;

    public SetInventoryUseCase(InventoryRepositoryPort repo) {
        this.repo = repo;
    }

    public Inventory execute(Long productId, Long qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        return repo.set(productId, qty);
    }
}
