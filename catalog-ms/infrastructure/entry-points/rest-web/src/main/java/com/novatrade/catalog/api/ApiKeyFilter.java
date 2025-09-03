package com.novatrade.catalog.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para validar la API key en las peticiones entrantes
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String ENV_API_KEY = "CATALOG_API_KEY";
    private static final String CONTENT_TYPE = "application/vnd.api+json";

    private final String expectedApiKey;

    /**
     * Constructor por defecto que obtiene la API key de las variables de entorno
     */
    public ApiKeyFilter() {
        this(System.getenv().getOrDefault(ENV_API_KEY, ""));
    }

    /**
     * Constructor para pruebas unitarias que permite inyectar la API key esperada
     * @param expectedApiKey La API key esperada
     */
    public ApiKeyFilter(String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (isInvalidApiKey(providedApiKey)) {
            sendUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si la API key proporcionada es inválida
     * @param providedApiKey La API key proporcionada en la petición
     * @return true si la API key es inválida, false en caso contrario
     */
    private boolean isInvalidApiKey(String providedApiKey) {
        return providedApiKey != null && !providedApiKey.isBlank() && !providedApiKey.equals(expectedApiKey);
    }

    /**
     * Envía una respuesta de error 401 Unauthorized
     * @param response El objeto HttpServletResponse
     * @throws IOException Si ocurre un error al escribir la respuesta
     */
    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().write(
                "{\"errors\":[{\"status\":\"401\",\"title\":\"Unauthorized\",\"detail\":\"Invalid API key\"}]}");
    }
}