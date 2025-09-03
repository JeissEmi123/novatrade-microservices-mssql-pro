package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductRepositoryPort repositoryPort;

    private ListProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListProductsUseCase(repositoryPort);
    }

    @Test
    @DisplayName("Should return list of products when products exist")
    void shouldReturnListOfProductsWhenProductsExist() {
        // Arrange
        List<Product> products = Arrays.asList(
            Product.builder()
                .id(1L)
                .name("Product 1")
                .price(99.99)
                .description(Optional.of("Description 1"))
                .build(),
            Product.builder()
                .id(2L)
                .name("Product 2")
                .price(149.99)
                .description(Optional.of("Description 2"))
                .build(),
            Product.builder()
                .id(3L)
                .name("Product 3")
                .price(199.99)
                .description(Optional.empty())
                .build()
        );

        when(repositoryPort.findAll()).thenReturn(products);

        // Act
        List<Product> result = useCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals(99.99, result.get(0).getPrice());
        assertTrue(result.get(0).getDescription().isPresent());
        assertEquals("Description 1", result.get(0).getDescription().get());

        assertEquals(3L, result.get(2).getId());
        assertFalse(result.get(2).getDescription().isPresent());

        verify(repositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExist() {
        // Arrange
        when(repositoryPort.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = useCase.execute();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repositoryPort, times(1)).findAll();
    }
}
