package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetInventoryUseCaseTest {

    @Mock
    private InventoryRepositoryPort repositoryPort;

    private GetInventoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetInventoryUseCase(repositoryPort);
    }

    @Test
    void shouldGetInventoryWhenProductExists() {
        // Arrange
        Long productId = 1L;
        Inventory expectedInventory = Inventory.builder()
                .productId(productId)
                .quantity(10L)
                .build();
        when(repositoryPort.getOrCreate(productId)).thenReturn(expectedInventory);

        // Act
        Inventory result = useCase.execute(productId);

        // Assert
        assertEquals(expectedInventory, result);
        verify(repositoryPort).getOrCreate(productId);
    }

    @Test
    void shouldCreateInventoryWhenProductDoesNotExist() {
        // Arrange
        Long productId = 2L;
        Inventory newInventory = Inventory.builder()
                .productId(productId)
                .quantity(0L)
                .build();
        when(repositoryPort.getOrCreate(productId)).thenReturn(newInventory);

        // Act
        Inventory result = useCase.execute(productId);

        // Assert
        assertEquals(newInventory, result);
        verify(repositoryPort).getOrCreate(productId);
    }
}
