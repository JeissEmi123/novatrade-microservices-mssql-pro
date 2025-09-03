package com.novatrade.inventory.api;

import com.novatrade.inventory.model.Inventory;
import com.novatrade.inventory.model.PurchaseResult;
import com.novatrade.inventory.model.ports.IdempotencyPort;
import com.novatrade.inventory.usecase.GetInventoryUseCase;
import com.novatrade.inventory.usecase.PurchaseUseCase;
import com.novatrade.inventory.usecase.SetInventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private GetInventoryUseCase getInventoryUseCase;

    @Mock
    private SetInventoryUseCase setInventoryUseCase;

    @Mock
    private PurchaseUseCase purchaseUseCase;

    @Mock
    private IdempotencyPort idempotencyPort;

    @InjectMocks
    private InventoryController inventoryController;

    private Inventory testInventory;
    private PurchaseResult testPurchaseResult;

    @BeforeEach
    void setUp() {
        // Configuración común para las pruebas
        testInventory = Inventory.builder()
                .productId(1L)
                .quantity(10L)
                .build();

        testPurchaseResult = PurchaseResult.builder()
                .productId(1L)
                .productName("Test Product")
                .unitPrice(100.0)
                .quantity(2L)
                .totalPrice(200.0)
                .success(true)
                .purchasedAt(Instant.now().toString())
                .build();
    }

    @Test
    void getInventory_ShouldReturnInventoryData() {
        // Arrange
        when(getInventoryUseCase.execute(1L)).thenReturn(testInventory);

        // Act
        JsonApi result = inventoryController.get(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.data());
        Map<String, Object> data = (Map<String, Object>) result.data();
        assertEquals("inventory", data.get("type"));
        assertEquals("1", data.get("id"));
        Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
        assertEquals(10L, attributes.get("quantity"));

        // Verify
        verify(getInventoryUseCase).execute(1L);
    }

    @Test
    void patchInventory_ShouldUpdateAndReturnInventoryData() {
        // Arrange
        InventoryController.PatchBody.Attributes attributes =
                new InventoryController.PatchBody.Attributes(20L);
        InventoryController.PatchBody.Body body =
                new InventoryController.PatchBody.Body("inventory", attributes);
        InventoryController.PatchBody patchBody = new InventoryController.PatchBody(body);

        Inventory updatedInventory = Inventory.builder()
                .productId(1L)
                .quantity(20L)
                .build();

        when(setInventoryUseCase.execute(1L, 20L)).thenReturn(updatedInventory);

        // Act
        JsonApi result = inventoryController.patch(1L, patchBody);

        // Assert
        assertNotNull(result);
        assertNotNull(result.data());
        Map<String, Object> data = (Map<String, Object>) result.data();
        assertEquals("inventory", data.get("type"));
        assertEquals("1", data.get("id"));
        Map<String, Object> resultAttributes = (Map<String, Object>) data.get("attributes");
        assertEquals(20L, resultAttributes.get("quantity"));

        // Verify
        verify(setInventoryUseCase).execute(1L, 20L);
    }

    @Test
    void purchase_WithoutIdempotencyKey_ShouldPurchaseSuccessfully() {
        // Arrange
        InventoryController.PurchaseBody.Attributes attributes =
                new InventoryController.PurchaseBody.Attributes(1L, 2L);
        InventoryController.PurchaseBody.Body body =
                new InventoryController.PurchaseBody.Body("purchase", attributes);
        InventoryController.PurchaseBody purchaseBody = new InventoryController.PurchaseBody(body);

        when(purchaseUseCase.execute(1L, 2L, null)).thenReturn(testPurchaseResult);

        // Act
        ResponseEntity<String> response = inventoryController.purchase(null, purchaseBody);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("\"productName\":\"Test Product\""));
        assertTrue(response.getBody().contains("\"quantity\":2"));
        assertTrue(response.getBody().contains("\"total\":200.0"));
        assertEquals(MediaType.valueOf("application/vnd.api+json"), response.getHeaders().getContentType());

        // Verify
        verify(purchaseUseCase).execute(1L, 2L, null);
        verify(idempotencyPort, never()).saveResponse(anyString(), anyString());
    }

    @Test
    void purchase_WithIdempotencyKey_ShouldSaveResponse() {
        // Arrange
        String idempotencyKey = "test-idem-key-123";
        InventoryController.PurchaseBody.Attributes attributes =
                new InventoryController.PurchaseBody.Attributes(1L, 2L);
        InventoryController.PurchaseBody.Body body =
                new InventoryController.PurchaseBody.Body("purchase", attributes);
        InventoryController.PurchaseBody purchaseBody = new InventoryController.PurchaseBody(body);

        when(idempotencyPort.findResponseByKey(idempotencyKey)).thenReturn(Optional.empty());
        when(purchaseUseCase.execute(1L, 2L, idempotencyKey)).thenReturn(testPurchaseResult);

        // Act
        ResponseEntity<String> response = inventoryController.purchase(idempotencyKey, purchaseBody);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Verify
        verify(idempotencyPort).findResponseByKey(idempotencyKey);
        verify(purchaseUseCase).execute(1L, 2L, idempotencyKey);
        verify(idempotencyPort).saveResponse(eq(idempotencyKey), anyString());
    }

    @Test
    void purchase_WithExistingIdempotencyKey_ShouldReturnCachedResponse() {
        // Arrange
        String idempotencyKey = "existing-idem-key";
        String cachedResponse = "{\"data\":{\"type\":\"purchases\",\"attributes\":{\"productName\":\"Cached Product\"}}}";
        InventoryController.PurchaseBody.Attributes attributes =
                new InventoryController.PurchaseBody.Attributes(1L, 2L);
        InventoryController.PurchaseBody.Body body =
                new InventoryController.PurchaseBody.Body("purchase", attributes);
        InventoryController.PurchaseBody purchaseBody = new InventoryController.PurchaseBody(body);

        when(idempotencyPort.findResponseByKey(idempotencyKey)).thenReturn(Optional.of(cachedResponse));

        // Act
        ResponseEntity<String> response = inventoryController.purchase(idempotencyKey, purchaseBody);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cachedResponse, response.getBody());
        assertEquals(MediaType.valueOf("application/vnd.api+json"), response.getHeaders().getContentType());

        // Verify
        verify(idempotencyPort).findResponseByKey(idempotencyKey);
        verify(purchaseUseCase, never()).execute(anyLong(), anyLong(), anyString());
        verify(idempotencyPort, never()).saveResponse(anyString(), anyString());
    }
}
