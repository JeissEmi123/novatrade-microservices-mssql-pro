package com.novatrade.inventory.api;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.novatrade.inventory.usecase.PurchaseUseCase.ServiceCommunicationException;
import com.novatrade.inventory.usecase.PurchaseUseCase.InsufficientInventoryException;

@RestControllerAdvice
public class JsonApiErrorHandler {
  record JsonApiError(String status, String title, String detail){}
  record Errors(List<JsonApiError> errors){}

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Errors> bad(IllegalArgumentException ex){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body(new Errors(List.of(new JsonApiError("400", "Bad Request", ex.getMessage()))));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> conflict(IllegalStateException ex){
    if(ex.getMessage()!=null && ex.getMessage().startsWith("__IDEMPOTENT_RETURN__")){
      var json=ex.getMessage().substring("__IDEMPOTENT_RETURN__".length());
      return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("application/vnd.api+json"))
        .body(json);
    }
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body("{\"errors\":[{\"status\":\"409\",\"title\":\"Conflict\",\"detail\":\"" + ex.getMessage() + "\"}]}");
  }

  @ExceptionHandler(java.util.NoSuchElementException.class)
  public ResponseEntity<Errors> notFound(java.util.NoSuchElementException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body(new Errors(List.of(new JsonApiError("404", "Not Found", ex.getMessage()))));
  }

  @ExceptionHandler(ServiceCommunicationException.class)
  public ResponseEntity<Errors> serviceUnavailable(ServiceCommunicationException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body(new Errors(List.of(new JsonApiError("503", "Service Unavailable", ex.getMessage()))));
  }

  @ExceptionHandler(InsufficientInventoryException.class)
  public ResponseEntity<Errors> insufficientInventory(InsufficientInventoryException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body(new Errors(List.of(new JsonApiError("422", "Unprocessable Entity", ex.getMessage()))));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Errors> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .contentType(MediaType.valueOf("application/vnd.api+json"))
      .body(new Errors(List.of(new JsonApiError("500", "Internal Server Error", "An unexpected error occurred"))));
  }
}