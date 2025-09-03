package com.novatrade.catalog.api;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.usecase.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/products", produces = "application/vnd.api+json")
@Validated
public class ProductController {
    private final CreateProductUseCase create;
    private final GetProductUseCase get;
    private final ListProductsUseCase list;

    public ProductController(CreateProductUseCase c, GetProductUseCase g, ListProductsUseCase l) {
        this.create = c;
        this.get = g;
        this.list = l;
    }

    record ProductBody(Body data) {
        record Body(String type, Attributes attributes) {}

        record Attributes(
            @NotBlank String name,
            @NotNull @Min(0) Double price,
            String description
        ) {}
    }

    @PostMapping(consumes = "application/vnd.api+json")
    public JsonApi create(@RequestBody ProductBody body) {
        var attributes = body.data().attributes();
        var product = Product.builder()
                .name(attributes.name())
                .price(attributes.price())
                .description(java.util.Optional.ofNullable(attributes.description()))
                .build();

        var saved = create.execute(product);
        return wrap(saved);
    }

    @GetMapping("/{id}")
    public JsonApi get(@PathVariable("id") Long id) {
        return wrap(get.execute(id));
    }

    @GetMapping
    public JsonApi list() {
        var items = list.execute().stream()
                .map(this::data)
                .toList();
        return JsonApi.of(items);
    }

    private JsonApi wrap(Product p) {
        return JsonApi.of(data(p));
    }

    private Map<String, Object> data(Product p) {
        return Map.of(
            "type", "products",
            "id", String.valueOf(p.getId()),
            "attributes", Map.of(
                "name", p.getName(),
                "price", p.getPrice(),
                "description", p.getDescription().orElse(null)
            )
        );
    }
}