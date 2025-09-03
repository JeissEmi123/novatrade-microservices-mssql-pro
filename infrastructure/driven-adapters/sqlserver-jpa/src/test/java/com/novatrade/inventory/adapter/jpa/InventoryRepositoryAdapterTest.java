package com.novatrade.inventory.adapter.jpa;

import com.novatrade.inventory.model.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para InventoryRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
public class InventoryRepositoryAdapterTest {

    private static final Long TEST_PRODUCT_ID = 123L;
    private static final Long TEST_QUANTITY = 10L;

    @Mock
    private InventoryJpaRepository repository;

    private InventoryRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new InventoryRepositoryAdapter(repository);
    }

    @Test
    void getOrCreateShouldReturnExistingInventory() {
        // Arrange
        InventoryEntity entity = new InventoryEntity();
        entity.setProductId(TEST_PRODUCT_ID);
        entity.setQuantity(TEST_QUANTITY);
        when(repository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.of(entity));

        // Act
        Inventory result = adapter.getOrCreate(TEST_PRODUCT_ID);

        // Assert
        assertEquals(TEST_PRODUCT_ID, result.getProductId());
        assertEquals(TEST_QUANTITY, result.getQuantity());
        verify(repository).findById(TEST_PRODUCT_ID);
        verify(repository, never()).save(any());
    }

    @Test
    void getOrCreateShouldCreateNewInventoryWhenNotExists() {
        // Arrange
        when(repository.findById(TEST_PRODUCT_ID)).thenReturn(Optional.empty());
        InventoryEntity savedEntity = new InventoryEntity();
        savedEntity.setProductId(TEST_PRODUCT_ID);
        savedEntity.setQuantity(0L);
        when(repository.save(any(InventoryEntity.class))).thenReturn(savedEntity);

        // Act
        Inventory result = adapter.getOrCreate(TEST_PRODUCT_ID);

        // Assert
        assertEquals(TEST_PRODUCT_ID, result.getProductId());
        assertEquals(0L, result.getQuantity());

        ArgumentCaptor<InventoryEntity> entityCaptor = ArgumentCaptor.forClass(InventoryEntity.class);
        verify(repository).save(entityCaptor.capture());

        InventoryEntity capturedEntity = entityCaptor.getValue();
        assertEquals(TEST_PRODUCT_ID, capturedEntity.getProductId());
        assertEquals(0L, capturedEntity.getQuantity());
    }

    @Test
    void setShouldUpdateInventoryQuantity() {
        // Arrange
        InventoryEntity savedEntity = new InventoryEntity();
        savedEntity.setProductId(TEST_PRODUCT_ID);
        savedEntity.setQuantity(TEST_QUANTITY);
        when(repository.save(any(InventoryEntity.class))).thenReturn(savedEntity);

        // Act
        Inventory result = adapter.set(TEST_PRODUCT_ID, TEST_QUANTITY);

        // Assert
        assertEquals(TEST_PRODUCT_ID, result.getProductId());
        assertEquals(TEST_QUANTITY, result.getQuantity());

        ArgumentCaptor<InventoryEntity> entityCaptor = ArgumentCaptor.forClass(InventoryEntity.class);
        verify(repository).save(entityCaptor.capture());

        InventoryEntity capturedEntity = entityCaptor.getValue();
        assertEquals(TEST_PRODUCT_ID, capturedEntity.getProductId());
        assertEquals(TEST_QUANTITY, capturedEntity.getQuantity());
    }

    @Test
    void decrementIfEnoughShouldReturnTrueWhenQuantityDecremented() {
        // Arrange
        when(repository.decrementIfEnough(TEST_PRODUCT_ID, 5L)).thenReturn(1);

        // Act
        boolean result = adapter.decrementIfEnough(TEST_PRODUCT_ID, 5L);

        // Assert
        assertTrue(result);
        verify(repository).decrementIfEnough(TEST_PRODUCT_ID, 5L);
    }

    @Test
    void decrementIfEnoughShouldReturnFalseWhenNotEnoughStock() {
        // Arrange
        when(repository.decrementIfEnough(TEST_PRODUCT_ID, 20L)).thenReturn(0);

        // Act
        boolean result = adapter.decrementIfEnough(TEST_PRODUCT_ID, 20L);

        // Assert
        assertFalse(result);
        verify(repository).decrementIfEnough(TEST_PRODUCT_ID, 20L);
    }
}
