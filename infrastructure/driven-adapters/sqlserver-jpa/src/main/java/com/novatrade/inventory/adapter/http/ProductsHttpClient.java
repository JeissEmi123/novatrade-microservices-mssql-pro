package com.novatrade.inventory.adapter.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novatrade.inventory.model.ports.ProductsClientPort;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProductsHttpClient implements ProductsClientPort {

    private static final Logger log = LoggerFactory.getLogger(ProductsHttpClient.class);
    private final RestTemplate restTemplate;
    private final String catalogBaseUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public ProductsHttpClient(
            RestTemplate restTemplate,
            @Value("${services.catalog.url}") String catalogBaseUrl,
            @Value("${services.catalog.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.catalogBaseUrl = catalogBaseUrl;
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Retry(name = "productService", fallbackMethod = "getProductFallback")
    public ProductDTO getProduct(Long id) {
        log.info("Requesting product with ID {} from catalog service", id);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("Accept", "application/vnd.api+json");

        var request = new org.springframework.http.HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(
                    catalogBaseUrl + "/api/v1/products/" + id,
                    org.springframework.http.HttpMethod.GET,
                    request,
                    String.class
            );

            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode data = root.get("data");
                if (data == null) {
                    throw new RuntimeException("Failed to parse response: missing data field");
                }
                JsonNode attributes = data.get("attributes");
                if (attributes == null || !attributes.has("name") || !attributes.has("price")) {
                    throw new RuntimeException("Failed to parse response: missing required attributes");
                }

                return new ProductDTO(
                        Long.parseLong(data.get("id").asText()),
                        attributes.get("name").asText(),
                        attributes.get("price").asDouble()
                );
            } catch (RuntimeException e) {
                log.error("Error retrieving product with ID {}: {}", id, e.getMessage());
                throw e; // Propagar RuntimeException
            } catch (Exception e) {
                log.error("Error retrieving product with ID {}: {}", id, e.getMessage());
                throw new RuntimeException("Failed to parse response: " + e.getMessage());
            }
        } catch (HttpClientErrorException e) {
            // Manejo específico para error 404 Not Found
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("Product with ID {} not found: {}", id, e.getMessage());
                throw new NoSuchElementException("Product not found with ID: " + id);
            }
            log.error("Error retrieving product with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve product with ID: " + id + ". Error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Connection error while retrieving product with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to connect to product service: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving product with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve product with ID: " + id + ". Error: " + e.getMessage());
        }
    }

    private ProductDTO getProductFallback(Long id, Exception e) {
        log.error("Fallback for getProduct. Product service unavailable for ID: {}", id);
        if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new NoSuchElementException("Product not found with ID: " + id);
        }
        throw new RuntimeException("Product service unavailable. Could not retrieve product with ID: " + id);
    }
}
