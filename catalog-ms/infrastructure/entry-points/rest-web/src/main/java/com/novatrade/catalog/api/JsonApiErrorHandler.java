package com.novatrade.catalog.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador global de excepciones que convierte las excepciones en respuestas JSON API compatibles.
 */
@RestControllerAdvice
public class JsonApiErrorHandler {

    /**
     * Representa un error individual en formato JSON API
     */
    record JsonApiError(String status, String title, String detail) {}

    /**
     * Colección de errores según la especificación JSON API
     */
    record Errors(List<JsonApiError> errors) {}

    /**
     * Media type constante para JSON API
     */
    private static final MediaType JSON_API_MEDIA_TYPE = MediaType.valueOf("application/vnd.api+json");

    /**
     * Método auxiliar para crear una respuesta de error
     */
    private ResponseEntity<Errors> createErrorResponse(HttpStatus status, String title, String detail) {
        return ResponseEntity
                .status(status)
                .contentType(JSON_API_MEDIA_TYPE)
                .body(new Errors(List.of(new JsonApiError(
                        String.valueOf(status.value()),
                        title,
                        detail
                ))));
    }

    /**
     * Maneja excepciones de argumento ilegal (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Errors> handleBadRequest(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    /**
     * Maneja excepciones de elemento no encontrado (404 Not Found)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Errors> handleNotFound(NoSuchElementException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    /**
     * Maneja excepciones de validación de argumentos (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationErrors(MethodArgumentNotValidException ex) {
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Error de validación en los datos enviados"
        );
    }

    /**
     * Maneja cualquier otra excepción no controlada (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Errors> handleGenericException(Exception ex) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Se produjo un error interno en el servidor"
        );
    }
}