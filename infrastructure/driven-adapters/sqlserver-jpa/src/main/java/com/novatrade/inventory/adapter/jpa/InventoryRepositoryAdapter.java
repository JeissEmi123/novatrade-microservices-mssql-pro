package com.novatrade.inventory.adapter.jpa;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador para el repositorio de inventario
 * Implementa el puerto de repositorio de inventario definido en el dominio
 */
@Component
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final InventoryJpaRepository repository;

    /**
     * Constructor que recibe el repositorio JPA
     *
     * @param repository Repositorio JPA para acceso a datos de inventario
     */
    public InventoryRepositoryAdapter(InventoryJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Convierte una entidad JPA a un objeto de dominio
     *
     * @param entity Entidad JPA de inventario
     * @return Objeto de dominio Inventory
     */
    private Inventory toDomain(InventoryEntity entity) {
        return Inventory.builder()
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .build();
    }

    /**
     * Obtiene un registro de inventario por ID o crea uno nuevo si no existe
     *
     * @param productId ID del producto
     * @return Objeto de dominio Inventory
     */
    @Override
    public Inventory getOrCreate(Long productId) {
        InventoryEntity entity = repository.findById(productId).orElseGet(() -> {
            InventoryEntity newEntity = new InventoryEntity();
            newEntity.setProductId(productId);
            newEntity.setQuantity(0L);
            return repository.save(newEntity);
        });
        return toDomain(entity);
    }

    /**
     * Establece la cantidad en inventario para un producto específico
     *
     * @param productId ID del producto
     * @param quantity Nueva cantidad a establecer
     * @return Objeto de dominio Inventory actualizado
     */
    @Override
    public Inventory set(Long productId, Long quantity) {
        InventoryEntity entity = new InventoryEntity();
        entity.setProductId(productId);
        entity.setQuantity(quantity);
        return toDomain(repository.save(entity));
    }

    /**
     * Decrementa la cantidad de un producto si hay suficiente stock
     *
     * @param productId ID del producto
     * @param quantity Cantidad a decrementar
     * @return true si se pudo decrementar, false si no hay suficiente stock
     */
    @Override
    public boolean decrementIfEnough(Long productId, Long quantity) {
        return repository.decrementIfEnough(productId, quantity) > 0;
    }
}
