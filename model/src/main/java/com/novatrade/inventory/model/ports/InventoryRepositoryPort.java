package com.novatrade.inventory.model.ports;
import com.novatrade.inventory.model.Inventory;
public interface InventoryRepositoryPort {
    Inventory getOrCreate(Long productId);
    Inventory set(Long productId, Long quantity);
    boolean decrementIfEnough(Long productId, Long qty);
}
