package com.novatrade.inventory.adapter.http;

import com.novatrade.inventory.model.ports.ProductsClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsHttpClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ProductsHttpClient client;
    private final String baseUrl = "http://catalog-ms:8081";
    private final String apiKey = "test_api_key";

    @BeforeEach
    void setUp() {
        client = new ProductsHttpClient(restTemplate, baseUrl, apiKey);
    }

    @Test
    @DisplayName("Should return product when catalog service responds correctly")
    void shouldReturnProductWhenCatalogServiceRespondsCorrectly() {
        // Given
        String jsonResponse = """
                {
                  "data": {
                    "type": "products",
                    "id": "1",
                    "attributes": {
                      "name": "Test Product",
                      "price": 100.0,
                      "description": "Product description"
                    }
                  }
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(responseEntity);

        // When
        ProductsClientPort.ProductDTO product = client.getProduct(1L);

        // Then
        assertNotNull(product);
        assertEquals(1L, product.id());
        assertEquals("Test Product", product.name());
        assertEquals(100.0, product.price(), 0.001);

        // Verify correct URL and headers were used
        verify(restTemplate).exchange(
            eq(baseUrl + "/api/v1/products/1"),
            eq(HttpMethod.GET),
            argThat(entity -> {
                assertEquals(apiKey, entity.getHeaders().getFirst("X-API-Key"));
                assertEquals("application/vnd.api+json", entity.getHeaders().getFirst("Accept"));
                return true;
            }),
            eq(String.class)
        );
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when product is not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        // When and Then
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> client.getProduct(999L)
        );

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when connection fails")
    void shouldThrowRuntimeExceptionWhenConnectionFails() {
        // Given
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        // When and Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> client.getProduct(1L)
        );

        assertTrue(exception.getMessage().contains("Failed to connect"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when response body is invalid")
    void shouldThrowRuntimeExceptionWhenResponseBodyIsInvalid() {
        // Given
        String invalidJsonResponse = "{ invalid json }";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(responseEntity);

        // When and Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> client.getProduct(1L)
        );

        assertTrue(exception.getMessage().contains("Failed to parse"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when response is missing required data")
    void shouldThrowRuntimeExceptionWhenResponseMissingData() {
        // Given
        String incompleteJsonResponse = """
                {
                  "data": {
                    "type": "products",
                    "id": "1",
                    "attributes": {
                      "name": "Test Product"
                      // missing price
                    }
                  }
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(incompleteJsonResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(responseEntity);

        // When and Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> client.getProduct(1L)
        );

        assertTrue(exception.getMessage().contains("Failed to parse"));
    }
}
