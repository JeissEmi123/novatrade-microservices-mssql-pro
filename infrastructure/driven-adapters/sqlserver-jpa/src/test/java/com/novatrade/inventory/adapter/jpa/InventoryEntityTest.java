package com.novatrade.inventory.adapter.jpa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase InventoryEntity
 */
public class InventoryEntityTest {

    private static final Long TEST_PRODUCT_ID = 123L;
    private static final Long TEST_QUANTITY = 10L;
    private static final Long TEST_VERSION = 1L;

    @Test
    public void noArgsConstructorShouldCreateEmptyEntity() {
        // Act
        InventoryEntity entity = new InventoryEntity();

        // Assert
        assertNull(entity.getProductId());
        assertNull(entity.getQuantity());
        assertNull(entity.getVersion());
    }

    @Test
    public void gettersAndSettersShouldWorkProperly() {
        // Arrange
        InventoryEntity entity = new InventoryEntity();

        // Act
        entity.setProductId(TEST_PRODUCT_ID);
        entity.setQuantity(TEST_QUANTITY);
        entity.setVersion(TEST_VERSION);

        // Assert
        assertEquals(TEST_PRODUCT_ID, entity.getProductId());
        assertEquals(TEST_QUANTITY, entity.getQuantity());
        assertEquals(TEST_VERSION, entity.getVersion());
    }

    @Test
    public void allArgsConstructorShouldInitializeAllFields() {
        // Act
        InventoryEntity entity = new InventoryEntity(TEST_PRODUCT_ID, TEST_QUANTITY, TEST_VERSION);

        // Assert
        assertEquals(TEST_PRODUCT_ID, entity.getProductId());
        assertEquals(TEST_QUANTITY, entity.getQuantity());
        assertEquals(TEST_VERSION, entity.getVersion());
    }

    @Test
    public void builderShouldCreateValidEntity() {
        // Act
        InventoryEntity entity = InventoryEntity.builder()
                .productId(TEST_PRODUCT_ID)
                .quantity(TEST_QUANTITY)
                .version(TEST_VERSION)
                .build();

        // Assert
        assertEquals(TEST_PRODUCT_ID, entity.getProductId());
        assertEquals(TEST_QUANTITY, entity.getQuantity());
        assertEquals(TEST_VERSION, entity.getVersion());
    }
}
