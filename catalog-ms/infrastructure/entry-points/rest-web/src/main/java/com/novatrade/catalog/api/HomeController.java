package com.novatrade.catalog.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para manejar la ruta raíz y proporcionar información básica sobre la API
 */
@Controller
public class HomeController {

    /**
     * Endpoint para la ruta raíz que devuelve información básica sobre la API
     * @return Información sobre la API en formato JSON
     */
    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> links = new HashMap<>();

        links.put("products", "/api/v1/products");
        links.put("swagger", "/swagger-ui.html");

        response.put("name", "Novatrade Catalog API");
        response.put("version", "1.0.0");
        response.put("status", "online");
        response.put("endpoints", links);

        return response;
    }

    /**
     * Redirige a los usuarios a la ruta API principal si acceden a /api
     */
    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> api() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bienvenido a la API de Novatrade Catalog");
        response.put("version", "v1");
        response.put("endpoints", Map.of("products", "/api/v1/products"));

        return response;
    }
}
