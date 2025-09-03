package com.novatrade.inventory.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTest {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_API_KEY = "test-valid-key";
    private static final String INVALID_API_KEY = "invalid-key";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ApiKeyFilter apiKeyFilter;
    private String originalApiKeyValue;

    @BeforeEach
    void setUp() {
        // Guardamos el valor original de la variable de entorno si existe
        originalApiKeyValue = System.getenv("INVENTORY_API_KEY");

        // Configuramos una variable de entorno simulada para las pruebas
        mockEnvironmentVariable("INVENTORY_API_KEY", VALID_API_KEY);

        // Creamos una instancia nueva del filtro para cada prueba
        apiKeyFilter = new ApiKeyFilter();
    }

    @AfterEach
    void tearDown() {
        // Restauramos la variable de entorno a su valor original
        if (originalApiKeyValue != null) {
            mockEnvironmentVariable("INVENTORY_API_KEY", originalApiKeyValue);
        } else {
            clearEnvironmentVariable("INVENTORY_API_KEY");
        }
    }

    @Test
    void debePermitirSolicitudCuandoApiKeyEsValida() throws ServletException, IOException {
        // Preparación
        when(request.getHeader(API_KEY_HEADER)).thenReturn(VALID_API_KEY);

        // Ejecución
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Verificación
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void debePermitirSolicitudCuandoNoSeProveeApiKey() throws ServletException, IOException {
        // Preparación
        when(request.getHeader(API_KEY_HEADER)).thenReturn(null);

        // Ejecución
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Verificación
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void debePermitirSolicitudCuandoApiKeyEstaVacia() throws ServletException, IOException {
        // Preparación
        when(request.getHeader(API_KEY_HEADER)).thenReturn("");

        // Ejecución
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Verificación
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void debeRetornarNoAutorizadoCuandoApiKeyEsInvalida() throws ServletException, IOException {
        // Preparación
        when(request.getHeader(API_KEY_HEADER)).thenReturn(INVALID_API_KEY);

        // Preparamos un StringWriter para capturar la respuesta
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Ejecución
        apiKeyFilter.doFilterInternal(request, response, filterChain);

        // Verificación
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/vnd.api+json");

        // Verificamos el contenido de la respuesta JSON
        String responseContent = stringWriter.toString();
        assertTrue(responseContent.contains("\"status\":\"401\""));
        assertTrue(responseContent.contains("\"title\":\"Unauthorized\""));
        assertTrue(responseContent.contains("\"detail\":\"Invalid API key\""));
    }

    // Métodos auxiliares para simular variables de entorno
    private void mockEnvironmentVariable(String name, String value) {
        try {
            // Esta es una forma de simular una variable de entorno en pruebas
            // Nota: Requiere permisos y puede no funcionar en todos los entornos
            System.setProperty(name, value);
        } catch (Exception e) {
            System.err.println("No se pudo simular la variable de entorno: " + e.getMessage());
        }
    }

    private void clearEnvironmentVariable(String name) {
        System.clearProperty(name);
    }
}
