package com.novatrade.catalog;

import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import com.novatrade.catalog.usecase.CreateProductUseCase;
import com.novatrade.catalog.usecase.GetProductUseCase;
import com.novatrade.catalog.usecase.ListProductsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private ProductRepositoryPort repositoryPort;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring se carga correctamente
        assertNotNull(context);
    }

    @Test
    void createProductUseCaseBeanIsCreated() {
        // Verifica que el bean CreateProductUseCase se ha creado correctamente
        CreateProductUseCase useCase = context.getBean(CreateProductUseCase.class);
        assertNotNull(useCase);
    }

    @Test
    void getProductUseCaseBeanIsCreated() {
        // Verifica que el bean GetProductUseCase se ha creado correctamente
        GetProductUseCase useCase = context.getBean(GetProductUseCase.class);
        assertNotNull(useCase);
    }

    @Test
    void listProductsUseCaseBeanIsCreated() {
        // Verifica que el bean ListProductsUseCase se ha creado correctamente
        ListProductsUseCase useCase = context.getBean(ListProductsUseCase.class);
        assertNotNull(useCase);
    }

    @Test
    void mainMethodDoesNotThrowException() {
        // Verifica que el método main no lanza excepciones
        Application.main(new String[]{});
    }
}
