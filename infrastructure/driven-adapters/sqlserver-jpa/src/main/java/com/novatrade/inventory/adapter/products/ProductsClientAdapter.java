package com.novatrade.inventory.adapter.products;

import com.novatrade.inventory.model.ports.ProductsClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ProductsClientAdapter implements ProductsClientPort {

    private final RestClient client;
    private final String apiKey;

    public ProductsClientAdapter(
            @Value("${products.base-url:http://localhost:8081}") String baseUrl,
            @Value("${products.api-key:}") String apiKey) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.api+json")
                .build();
        this.apiKey = apiKey;
    }

    @Override
    @CircuitBreaker(name = "products")
    @Retry(name = "products")
    public ProductDTO getProduct(Long id) {
        var resp = client.get()
                .uri("/api/v1/products/{id}", id)
                .header("X-API-KEY", apiKey)
                .retrieve()
                .toEntity(Map.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return null;
        }

        var body = resp.getBody();
        var data = (Map<?, ?>) body.get("data");
        if (data == null) {
            return null;
        }

        var attrs = (Map<?, ?>) data.get("attributes");
        if (attrs == null) {
            return null;
        }

        return new ProductDTO(
                Long.valueOf((String) data.get("id")),
                (String) attrs.get("name"),
                ((Number) attrs.get("price")).doubleValue());
    }
}
