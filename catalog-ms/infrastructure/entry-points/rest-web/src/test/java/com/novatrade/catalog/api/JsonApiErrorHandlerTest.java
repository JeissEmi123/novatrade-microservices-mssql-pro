package com.novatrade.catalog.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para JsonApiErrorHandler
 */
class JsonApiErrorHandlerTest {

    private final JsonApiErrorHandler errorHandler = new JsonApiErrorHandler();

    @Test
    void handleBadRequest_debeRetornarEstadoBadRequest() {
        // Arrange
        String mensajeError = "Parámetro inválido";
        IllegalArgumentException exception = new IllegalArgumentException(mensajeError);

        // Act
        ResponseEntity<?> response = errorHandler.handleBadRequest(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof JsonApiErrorHandler.Errors);
        JsonApiErrorHandler.Errors errors = (JsonApiErrorHandler.Errors) response.getBody();
        assertEquals(1, errors.errors().size());
        assertEquals("400", errors.errors().get(0).status());
        assertEquals("Bad Request", errors.errors().get(0).title());
        assertEquals(mensajeError, errors.errors().get(0).detail());
    }

    @Test
    void handleNotFound_debeRetornarEstadoNotFound() {
        // Arrange
        String mensajeError = "Recurso no encontrado";
        NoSuchElementException exception = new NoSuchElementException(mensajeError);

        // Act
        ResponseEntity<?> response = errorHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof JsonApiErrorHandler.Errors);
        JsonApiErrorHandler.Errors errors = (JsonApiErrorHandler.Errors) response.getBody();
        assertEquals(1, errors.errors().size());
        assertEquals("404", errors.errors().get(0).status());
        assertEquals("Not Found", errors.errors().get(0).title());
        assertEquals(mensajeError, errors.errors().get(0).detail());
    }

    @Test
    void handleValidationErrors_debeRetornarEstadoBadRequest() {
        // Arrange
        MethodArgumentNotValidException exception = null; // Mock no se puede crear directamente

        // Act
        ResponseEntity<?> response = errorHandler.handleValidationErrors(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof JsonApiErrorHandler.Errors);
        JsonApiErrorHandler.Errors errors = (JsonApiErrorHandler.Errors) response.getBody();
        assertEquals(1, errors.errors().size());
        assertEquals("400", errors.errors().get(0).status());
        assertEquals("Validation Error", errors.errors().get(0).title());
        assertEquals("Error de validación en los datos enviados", errors.errors().get(0).detail());
    }

    @Test
    void handleGenericException_debeRetornarEstadoInternalServerError() {
        // Arrange
        Exception exception = new RuntimeException("Error interno");

        // Act
        ResponseEntity<?> response = errorHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof JsonApiErrorHandler.Errors);
        JsonApiErrorHandler.Errors errors = (JsonApiErrorHandler.Errors) response.getBody();
        assertEquals(1, errors.errors().size());
        assertEquals("500", errors.errors().get(0).status());
        assertEquals("Internal Server Error", errors.errors().get(0).title());
        assertEquals("Se produjo un error interno en el servidor", errors.errors().get(0).detail());
    }
}
