package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetInventoryUseCaseTest {

    @Mock
    private InventoryRepositoryPort repositoryPort;

    private SetInventoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SetInventoryUseCase(repositoryPort);
    }

    @Test
    void shouldSetInventoryWhenQuantityIsValid() {
        // Arrange
        Long productId = 1L;
        Long quantity = 10L;
        Inventory expectedInventory = Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
        when(repositoryPort.set(productId, quantity)).thenReturn(expectedInventory);

        // Act
        Inventory result = useCase.execute(productId, quantity);

        // Assert
        assertEquals(expectedInventory, result);
        verify(repositoryPort).set(productId, quantity);
    }

    @Test
    void shouldSetInventoryToZeroWhenQuantityIsZero() {
        // Arrange
        Long productId = 2L;
        Long quantity = 0L;
        Inventory expectedInventory = Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
        when(repositoryPort.set(productId, quantity)).thenReturn(expectedInventory);

        // Act
        Inventory result = useCase.execute(productId, quantity);

        // Assert
        assertEquals(expectedInventory, result);
        verify(repositoryPort).set(productId, quantity);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Arrange
        Long productId = 3L;
        Long quantity = -1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(productId, quantity)
        );

        assertEquals("quantity must be >= 0", exception.getMessage());
        verify(repositoryPort, never()).set(any(), any());
    }
}
