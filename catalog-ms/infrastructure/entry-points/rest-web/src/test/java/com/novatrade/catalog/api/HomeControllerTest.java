package com.novatrade.catalog.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pruebas unitarias para el HomeController
 */
@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    public void testHome() {
        // Ejecutar
        Map<String, Object> response = homeController.home();

        // Verificar
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals("Novatrade Catalog API", response.get("name"), "El nombre de la API debe ser correcto");
        assertEquals("1.0.0", response.get("version"), "La versión debe ser correcta");
        assertEquals("online", response.get("status"), "El estado debe ser correcto");

        // Verificar endpoints
        Map<String, String> endpoints = (Map<String, String>) response.get("endpoints");
        assertNotNull(endpoints, "Los endpoints no deben ser nulos");
        assertEquals("/api/v1/products", endpoints.get("products"), "La URL de productos debe ser correcta");
        assertEquals("/swagger-ui.html", endpoints.get("swagger"), "La URL de swagger debe ser correcta");
    }

    @Test
    public void testApi() {
        // Ejecutar
        Map<String, Object> response = homeController.api();

        // Verificar
        assertNotNull(response, "La respuesta no debe ser nula");
        assertEquals("Bienvenido a la API de Novatrade Catalog", response.get("message"), "El mensaje debe ser correcto");
        assertEquals("v1", response.get("version"), "La versión debe ser correcta");

        // Verificar endpoints
        Map<String, String> endpoints = (Map<String, String>) response.get("endpoints");
        assertNotNull(endpoints, "Los endpoints no deben ser nulos");
        assertEquals("/api/v1/products", endpoints.get("products"), "La URL de productos debe ser correcta");
    }
}
