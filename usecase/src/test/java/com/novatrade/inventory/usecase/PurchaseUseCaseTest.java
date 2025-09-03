package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.PurchaseResult;
import com.novatrade.inventory.model.ports.IdempotencyPort;
import com.novatrade.inventory.model.ports.InventoryRepositoryPort;
import com.novatrade.inventory.model.ports.ProductsClientPort;
import com.novatrade.inventory.usecase.PurchaseUseCase.InsufficientInventoryException;
import com.novatrade.inventory.usecase.PurchaseUseCase.ServiceCommunicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseUseCaseTest {

    @Mock
    private InventoryRepositoryPort inventoryRepository;

    @Mock
    private ProductsClientPort productsClient;

    @Mock
    private IdempotencyPort idempotencyService;

    private PurchaseUseCase useCase;

    private final Long PRODUCT_ID = 1L;
    private final Long QUANTITY = 5L;
    private final String IDEMPOTENCY_KEY = "test-key-123";
    private final ProductsClientPort.ProductDTO PRODUCT = new ProductsClientPort.ProductDTO(PRODUCT_ID, "Test Product", 10.0);

    @BeforeEach
    void setUp() {
        useCase = new PurchaseUseCase(inventoryRepository, productsClient, idempotencyService);
    }

    @Test
    void shouldCompletePurchaseSuccessfully() {
        // Arrange
        when(idempotencyService.findResponseByKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(productsClient.getProduct(PRODUCT_ID)).thenReturn(PRODUCT);
        when(inventoryRepository.decrementIfEnough(PRODUCT_ID, QUANTITY)).thenReturn(true);

        // Act
        PurchaseResult result = useCase.execute(PRODUCT_ID, QUANTITY, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getProductId());
        assertEquals(PRODUCT.name(), result.getProductName());
        assertEquals(PRODUCT.price(), result.getUnitPrice());
        assertEquals(QUANTITY, result.getQuantity());
        assertEquals(PRODUCT.price() * QUANTITY, result.getTotalPrice());
        assertTrue(result.isSuccess());
        assertNotNull(result.getPurchasedAt());

        verify(idempotencyService).findResponseByKey(IDEMPOTENCY_KEY);
        verify(productsClient).getProduct(PRODUCT_ID);
        verify(inventoryRepository).decrementIfEnough(PRODUCT_ID, QUANTITY);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(PRODUCT_ID, 0L, IDEMPOTENCY_KEY)
        );

        assertEquals("quantity must be > 0", exception.getMessage());
        verifyNoInteractions(productsClient);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(PRODUCT_ID, null, IDEMPOTENCY_KEY)
        );

        assertEquals("quantity must be > 0", exception.getMessage());
        verifyNoInteractions(productsClient);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void shouldThrowExceptionForIdempotentRequest() {
        // Arrange
        String cachedResponse = "{\"productId\":1,\"success\":true}";
        when(idempotencyService.findResponseByKey(IDEMPOTENCY_KEY)).thenReturn(Optional.of(cachedResponse));

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> useCase.execute(PRODUCT_ID, QUANTITY, IDEMPOTENCY_KEY)
        );

        assertTrue(exception.getMessage().startsWith("__IDEMPOTENT_RETURN__"));
        verify(idempotencyService).findResponseByKey(IDEMPOTENCY_KEY);
        verifyNoInteractions(productsClient);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void shouldSkipIdempotencyCheckWhenKeyIsNull() {
        // Arrange
        when(productsClient.getProduct(PRODUCT_ID)).thenReturn(PRODUCT);
        when(inventoryRepository.decrementIfEnough(PRODUCT_ID, QUANTITY)).thenReturn(true);

        // Act
        PurchaseResult result = useCase.execute(PRODUCT_ID, QUANTITY, null);

        // Assert
        assertNotNull(result);
        verifyNoInteractions(idempotencyService);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        when(idempotencyService.findResponseByKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(productsClient.getProduct(PRODUCT_ID)).thenReturn(null);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> useCase.execute(PRODUCT_ID, QUANTITY, IDEMPOTENCY_KEY)
        );

        assertEquals("product not found", exception.getMessage());
        verify(productsClient).getProduct(PRODUCT_ID);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void shouldPropagateProductClientExceptions() {
        // Arrange
        when(idempotencyService.findResponseByKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(productsClient.getProduct(PRODUCT_ID)).thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        ServiceCommunicationException exception = assertThrows(
            ServiceCommunicationException.class,
            () -> useCase.execute(PRODUCT_ID, QUANTITY, IDEMPOTENCY_KEY)
        );

        assertEquals("Error communicating with catalog service", exception.getMessage());
        verify(productsClient).getProduct(PRODUCT_ID);
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientInventory() {
        // Arrange
        when(idempotencyService.findResponseByKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(productsClient.getProduct(PRODUCT_ID)).thenReturn(PRODUCT);
        when(inventoryRepository.decrementIfEnough(PRODUCT_ID, QUANTITY)).thenReturn(false);

        // Act & Assert
        InsufficientInventoryException exception = assertThrows(
            InsufficientInventoryException.class,
            () -> useCase.execute(PRODUCT_ID, QUANTITY, IDEMPOTENCY_KEY)
        );

        assertEquals("Insufficient inventory for product " + PRODUCT_ID, exception.getMessage());
        verify(productsClient).getProduct(PRODUCT_ID);
        verify(inventoryRepository).decrementIfEnough(PRODUCT_ID, QUANTITY);
    }
}
