package com.novatrade.inventory.adapter.products;

import com.novatrade.inventory.model.ports.ProductsClientPort.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsClientAdapterTest {

    private RestClient mockRestClient;
    private ProductsClientAdapter adapter;
    private final String BASE_URL = "http://test-api.com";
    private final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        // Crear un mock directo de RestClient en lugar de usar anotaciones
        mockRestClient = mock(RestClient.class);

        // Configurar el adaptador con valores de prueba y el RestClient mockeado
        adapter = new ProductsClientAdapter(BASE_URL, API_KEY);

        // Inyectar el RestClient mockeado en el adaptador
        try {
            java.lang.reflect.Field clientField = ProductsClientAdapter.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(adapter, mockRestClient);
        } catch (Exception e) {
            fail("Error al inyectar el mock del RestClient: " + e.getMessage());
        }
    }

    @Test
    void getProduct_WhenSuccessfulResponse_ShouldReturnProductDTO() {
        // Configurar datos de prueba
        Long productId = 1L;
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "Test Product");
        attributes.put("price", 29.99);

        Map<String, Object> data = new HashMap<>();
        data.put("id", "1");
        data.put("attributes", attributes);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Configurar la cadena de mocks sin tipos genéricos complejos
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(mockRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(eq(Map.class))).thenReturn(responseEntity);

        // Ejecutar el método a probar
        ProductDTO result = adapter.getProduct(productId);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(29.99, result.price());

        // Verificar que los métodos del mock se llamaron correctamente
        verify(mockRestClient).get();
        verify(requestHeadersUriSpec).uri("/api/v1/products/{id}", productId);
        verify(requestHeadersSpec).header("X-API-KEY", API_KEY);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).toEntity(Map.class);
    }

    @Test
    void getProduct_WhenErrorResponse_ShouldReturnNull() {
        // Configurar datos de prueba
        Long productId = 1L;
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Configurar la cadena de mocks
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(mockRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(eq(Map.class))).thenReturn(responseEntity);

        // Ejecutar el método a probar
        ProductDTO result = adapter.getProduct(productId);

        // Verificar resultados
        assertNull(result);
    }

    @Test
    void getProduct_WhenMissingData_ShouldReturnNull() {
        // Configurar datos de prueba
        Long productId = 1L;
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Configurar la cadena de mocks
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(mockRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(eq(Map.class))).thenReturn(responseEntity);

        // Ejecutar el método a probar
        ProductDTO result = adapter.getProduct(productId);

        // Verificar resultados
        assertNull(result);
    }

    @Test
    void getProduct_WhenMissingAttributes_ShouldReturnNull() {
        // Configurar datos de prueba
        Long productId = 1L;
        Map<String, Object> data = new HashMap<>();
        data.put("id", "1");
        // No incluimos attributes

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        // Configurar la cadena de mocks
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(mockRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(eq(Map.class))).thenReturn(responseEntity);

        // Ejecutar el método a probar
        ProductDTO result = adapter.getProduct(productId);

        // Verificar resultados
        assertNull(result);
    }
}
