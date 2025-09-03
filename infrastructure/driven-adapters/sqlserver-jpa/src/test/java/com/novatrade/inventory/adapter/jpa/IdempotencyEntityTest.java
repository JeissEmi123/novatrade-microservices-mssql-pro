package com.novatrade.inventory.adapter.jpa;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase IdempotencyEntity
 */
public class IdempotencyEntityTest {

    private static final String TEST_ID = "test-idempotency-key-123";
    private static final String TEST_JSON = "{\"result\":\"success\",\"message\":\"Operación completada\"}";

    @Test
    public void constructorShouldInitializeAllFields() {
        // Act
        IdempotencyEntity entity = new IdempotencyEntity(TEST_ID, TEST_JSON);

        // Assert
        assertEquals(TEST_ID, entity.getId());
        assertEquals(TEST_JSON, entity.getResponseJson());
        assertNotNull(entity.getCreatedAt());
    }

    @Test
    public void gettersAndSettersShouldWorkProperly() {
        // Arrange
        IdempotencyEntity entity = new IdempotencyEntity();
        Instant now = Instant.now();

        // Act
        entity.setId(TEST_ID);
        entity.setResponseJson(TEST_JSON);
        entity.setCreatedAt(now);

        // Assert
        assertEquals(TEST_ID, entity.getId());
        assertEquals(TEST_JSON, entity.getResponseJson());
        assertEquals(now, entity.getCreatedAt());
    }

    @Test
    public void builderShouldCreateValidEntity() {
        // Arrange
        Instant now = Instant.now();

        // Act
        IdempotencyEntity entity = IdempotencyEntity.builder()
                .id(TEST_ID)
                .responseJson(TEST_JSON)
                .createdAt(now)
                .build();

        // Assert
        assertEquals(TEST_ID, entity.getId());
        assertEquals(TEST_JSON, entity.getResponseJson());
        assertEquals(now, entity.getCreatedAt());
    }

    @Test
    public void noArgsConstructorShouldCreateEmptyEntity() {
        // Act
        IdempotencyEntity entity = new IdempotencyEntity();

        // Assert
        assertNull(entity.getId());
        assertNull(entity.getResponseJson());
        assertNull(entity.getCreatedAt());
    }

    @Test
    public void allArgsConstructorShouldInitializeAllFields() {
        // Arrange
        Instant now = Instant.now();

        // Act
        IdempotencyEntity entity = new IdempotencyEntity(TEST_ID, now, TEST_JSON);

        // Assert
        assertEquals(TEST_ID, entity.getId());
        assertEquals(TEST_JSON, entity.getResponseJson());
        assertEquals(now, entity.getCreatedAt());
    }
}
