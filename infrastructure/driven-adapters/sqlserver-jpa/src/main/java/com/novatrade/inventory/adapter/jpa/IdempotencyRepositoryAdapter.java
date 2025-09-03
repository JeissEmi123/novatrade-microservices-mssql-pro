package com.novatrade.inventory.adapter.jpa;

import com.novatrade.inventory.model.ports.IdempotencyPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador para el repositorio de idempotencia
 * Implementa el puerto de idempotencia definido en el dominio
 */
@Component
public class IdempotencyRepositoryAdapter implements IdempotencyPort {

    private final IdempotencyJpaRepository repository;

    /**
     * Constructor que recibe el repositorio JPA
     *
     * @param repository Repositorio JPA para acceso a datos
     */
    public IdempotencyRepositoryAdapter(IdempotencyJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Busca una respuesta guardada por su clave de idempotencia
     *
     * @param key Clave de idempotencia
     * @return Respuesta JSON opcional asociada a la clave
     */
    @Override
    public Optional<String> findResponseByKey(String key) {
        return repository.findById(key)
                .map(IdempotencyEntity::getResponseJson);
    }

    /**
     * Guarda una respuesta asociada a una clave de idempotencia
     *
     * @param key Clave de idempotencia
     * @param responseJson Respuesta JSON a guardar
     */
    @Override
    public void saveResponse(String key, String responseJson) {
        IdempotencyEntity entity = new IdempotencyEntity(key, responseJson);
        repository.save(entity);
    }
}
