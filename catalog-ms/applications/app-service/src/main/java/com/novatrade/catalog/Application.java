package com.novatrade.catalog;

import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import com.novatrade.catalog.usecase.CreateProductUseCase;
import com.novatrade.catalog.usecase.GetProductUseCase;
import com.novatrade.catalog.usecase.ListProductsUseCase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CreateProductUseCase create(ProductRepositoryPort repo) {
        return new CreateProductUseCase(repo);
    }

    @Bean
    public GetProductUseCase get(ProductRepositoryPort repo) {
        return new GetProductUseCase(repo);
    }

    @Bean
    public ListProductsUseCase list(ProductRepositoryPort repo) {
        return new ListProductsUseCase(repo);
    }
}