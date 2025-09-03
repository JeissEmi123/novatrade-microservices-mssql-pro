package com.novatrade.inventory.adapter.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entidad para gestionar la idempotencia de operaciones
 * Almacena claves de idempotencia y sus respuestas asociadas
 */
@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyEntity {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false)
    private Instant createdAt;

    @Lob
    @Column(nullable = false)
    private String responseJson;

    /**
     * Constructor con id que inicializa createdAt automáticamente
     *
     * @param id Identificador único de la operación
     * @param responseJson Respuesta JSON asociada a la operación
     */
    public IdempotencyEntity(String id, String responseJson) {
        this.id = id;
        this.responseJson = responseJson;
        this.createdAt = Instant.now();
    }
}
