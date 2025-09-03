package com.novatrade.inventory.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.novatrade.inventory.usecase.PurchaseUseCase.InsufficientInventoryException;
import com.novatrade.inventory.usecase.PurchaseUseCase.ServiceCommunicationException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

class JsonApiErrorHandlerTest {

    private final JsonApiErrorHandler errorHandler = new JsonApiErrorHandler();
    private static final String JSON_API_MEDIA_TYPE = "application/vnd.api+json";

    @Test
    void testBadRequest_IllegalArgumentException() {
        // Arrange
        String errorMessage = "Invalid input parameter";
        IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

        // Act
        ResponseEntity<JsonApiErrorHandler.Errors> response = errorHandler.bad(ex);
        JsonApiErrorHandler.Errors body = response.getBody();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertNotNull(body);
        assertEquals(1, body.errors().size());
        assertEquals("400", body.errors().get(0).status());
        assertEquals("Bad Request", body.errors().get(0).title());
        assertEquals(errorMessage, body.errors().get(0).detail());
    }

    @Test
    void testConflict_IllegalStateException() {
        // Arrange
        String errorMessage = "Resource already exists";
        IllegalStateException ex = new IllegalStateException(errorMessage);

        // Act
        ResponseEntity<String> response = errorHandler.conflict(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertTrue(response.getBody().contains("409"));
        assertTrue(response.getBody().contains("Conflict"));
        assertTrue(response.getBody().contains(errorMessage));
    }

    @Test
    void testConflict_IdempotentReturn() {
        // Arrange
        String jsonResult = "{\"data\":{\"id\":\"123\",\"type\":\"product\"}}";
        IllegalStateException ex = new IllegalStateException("__IDEMPOTENT_RETURN__" + jsonResult);

        // Act
        ResponseEntity<String> response = errorHandler.conflict(ex);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertEquals(jsonResult, response.getBody());
    }

    @Test
    void testNotFound_NoSuchElementException() {
        // Arrange
        String errorMessage = "Resource not found";
        NoSuchElementException ex = new NoSuchElementException(errorMessage);

        // Act
        ResponseEntity<JsonApiErrorHandler.Errors> response = errorHandler.notFound(ex);
        JsonApiErrorHandler.Errors body = response.getBody();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertNotNull(body);
        assertEquals(1, body.errors().size());
        assertEquals("404", body.errors().get(0).status());
        assertEquals("Not Found", body.errors().get(0).title());
        assertEquals(errorMessage, body.errors().get(0).detail());
    }


    @Test
    void testInsufficientInventory_InsufficientInventoryException() {
        // Arrange
        String errorMessage = "Not enough items in stock";
        InsufficientInventoryException ex = new InsufficientInventoryException(errorMessage);

        // Act
        ResponseEntity<JsonApiErrorHandler.Errors> response = errorHandler.insufficientInventory(ex);
        JsonApiErrorHandler.Errors body = response.getBody();

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertNotNull(body);
        assertEquals(1, body.errors().size());
        assertEquals("422", body.errors().get(0).status());
        assertEquals("Unprocessable Entity", body.errors().get(0).title());
        assertEquals(errorMessage, body.errors().get(0).detail());
    }

    @Test
    void testHandleGeneral_Exception() {
        // Arrange
        Exception ex = new RuntimeException("Generic error");

        // Act
        ResponseEntity<JsonApiErrorHandler.Errors> response = errorHandler.handleGeneral(ex);
        JsonApiErrorHandler.Errors body = response.getBody();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(JSON_API_MEDIA_TYPE, response.getHeaders().getContentType().toString());
        assertNotNull(body);
        assertEquals(1, body.errors().size());
        assertEquals("500", body.errors().get(0).status());
        assertEquals("Internal Server Error", body.errors().get(0).title());
        assertEquals("An unexpected error occurred", body.errors().get(0).detail());
    }
}
