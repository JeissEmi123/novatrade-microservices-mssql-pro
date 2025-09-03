package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;

public class GetInventoryUseCase {
    private final InventoryRepositoryPort repo;

    public GetInventoryUseCase(InventoryRepositoryPort repo) {
        this.repo = repo;
    }

    public Inventory execute(Long productId) {
        return repo.getOrCreate(productId);
    }
}
