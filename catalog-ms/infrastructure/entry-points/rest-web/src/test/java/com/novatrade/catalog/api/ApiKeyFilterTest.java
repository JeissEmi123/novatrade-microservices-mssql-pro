package com.novatrade.catalog.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private static final String VALID_API_KEY = "valid-api-key";
    private static final String INVALID_API_KEY = "invalid-api-key";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // No configuramos el mock response.getWriter() aquí para evitar "Unnecessary stubbings"
    }

    @Test
    void shouldContinueFilterChainWhenApiKeyIsValid() throws ServletException, IOException {
        // Arrange
        ApiKeyFilter filter = new ApiKeyFilter(VALID_API_KEY);
        when(request.getHeader(API_KEY_HEADER)).thenReturn(VALID_API_KEY);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldContinueFilterChainWhenApiKeyIsNotProvided() throws ServletException, IOException {
        // Arrange
        ApiKeyFilter filter = new ApiKeyFilter(VALID_API_KEY);
        when(request.getHeader(API_KEY_HEADER)).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldContinueFilterChainWhenApiKeyIsEmpty() throws ServletException, IOException {
        // Arrange
        ApiKeyFilter filter = new ApiKeyFilter(VALID_API_KEY);
        when(request.getHeader(API_KEY_HEADER)).thenReturn("");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldReturnUnauthorizedWhenApiKeyIsInvalid() throws ServletException, IOException {
        // Arrange
        ApiKeyFilter filter = new ApiKeyFilter(VALID_API_KEY);
        when(request.getHeader(API_KEY_HEADER)).thenReturn(INVALID_API_KEY);

        // Configuramos el mock del PrintWriter solo para este test que lo necesita
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/vnd.api+json");

        printWriter.flush();
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("\"status\":\"401\""));
        assertTrue(responseBody.contains("\"title\":\"Unauthorized\""));
        assertTrue(responseBody.contains("\"detail\":\"Invalid API key\""));
    }

    @Test
    void shouldUseEmptyStringAsDefaultApiKey() throws ServletException, IOException {
        // Arrange - creamos un filtro con una API key vacía, similar a cuando no se define la variable de entorno
        ApiKeyFilter filter = new ApiKeyFilter("");
        when(request.getHeader(API_KEY_HEADER)).thenReturn(INVALID_API_KEY);

        // Configuramos el mock del PrintWriter para evitar NullPointerException
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert - cualquier API key debe ser rechazada cuando la API key esperada es vacía
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
