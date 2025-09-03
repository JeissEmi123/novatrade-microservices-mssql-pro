package com.novatrade.inventory.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
  private static final String API_KEY_HEADER = "X-API-KEY";
  private final String expectedApiKey = System.getenv().getOrDefault("INVENTORY_API_KEY","");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String providedApiKey = request.getHeader(API_KEY_HEADER);

    if (isApiKeyProvidedButInvalid(providedApiKey)) {
      sendUnauthorizedResponse(response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isApiKeyProvidedButInvalid(String apiKey) {
    return apiKey != null && !apiKey.isBlank() && !apiKey.equals(expectedApiKey);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/vnd.api+json");
    response.getWriter().write("{\"errors\":[{\"status\":\"401\",\"title\":\"Unauthorized\",\"detail\":\"Invalid API key\"}]}");
  }
}