package com.novatrade.inventory.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio JPA para la entidad de inventario
 * Gestiona la persistencia de los registros de inventario
 */
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    /**
     * Decrementa la cantidad de un producto en inventario si hay suficiente stock
     * Utiliza una consulta nativa para garantizar la atomicidad de la operación
     *
     * @param productId ID del producto a decrementar
     * @param quantity Cantidad a decrementar
     * @return Número de filas afectadas (1 si tuvo éxito, 0 si no había suficiente stock)
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE dbo.inventory SET quantity = quantity - :q WHERE product_id = :pid AND quantity >= :q",
           nativeQuery = true)
    int decrementIfEnough(@Param("pid") Long productId, @Param("q") Long quantity);
}
