package com.novatrade.inventory.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad de idempotencia
 * Gestiona la persistencia de las claves de idempotencia
 */
public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyEntity, String> {
}
