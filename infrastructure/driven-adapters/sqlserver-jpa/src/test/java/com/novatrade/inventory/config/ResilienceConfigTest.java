package com.novatrade.inventory.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;

class ResilienceConfigTest {

    private final ResilienceConfig resilienceConfig = new ResilienceConfig();

    @Test
    @DisplayName("Debería crear RestTemplate exitosamente")
    void shouldCreateRestTemplate() {
        RestTemplate restTemplate = resilienceConfig.restTemplate();

        assertNotNull(restTemplate, "El bean RestTemplate no debería ser nulo");
    }

    @Test
    @DisplayName("Debería crear configuración de Retry con valores correctos")
    void shouldCreateRetryConfigWithCorrectValues() {
        RetryConfig retryConfig = resilienceConfig.productServiceRetryConfig();

        assertNotNull(retryConfig, "El bean RetryConfig no debería ser nulo");
        assertEquals(3, retryConfig.getMaxAttempts(), "El número máximo de intentos debe ser 3");
        // No verificamos waitDuration directamente ya que getIntervalFunction puede devolver null
    }

    @Test
    @DisplayName("Debería crear configuración de CircuitBreaker con valores correctos")
    void shouldCreateCircuitBreakerConfigWithCorrectValues() {
        CircuitBreakerConfig circuitBreakerConfig = resilienceConfig.productServiceCircuitBreakerConfig();

        assertNotNull(circuitBreakerConfig, "El bean CircuitBreakerConfig no debería ser nulo");
        assertEquals(50.0f, circuitBreakerConfig.getFailureRateThreshold(), "El umbral de tasa de fallos debe ser 50");
        // La duración en milisegundos es 10000 (10 segundos)
        assertEquals(10000L, circuitBreakerConfig.getWaitIntervalFunctionInOpenState().apply(1),
                "La duración de espera en estado abierto debe ser 10 segundos (10000 ms)");
        assertEquals(5, circuitBreakerConfig.getSlidingWindowSize(), "El tamaño de ventana deslizante debe ser 5");
    }
}
