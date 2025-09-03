package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductUseCaseTest {

    @Mock
    private ProductRepositoryPort repositoryPort;

    private GetProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductUseCase(repositoryPort);
    }

    @Test
    @DisplayName("Should get product successfully when product exists")
    void shouldGetProductSuccessfully() {
        // Arrange
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(99.99)
                .description(Optional.of("Test Description"))
                .build();

        when(repositoryPort.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Product result = useCase.execute(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        assertTrue(result.getDescription().isPresent());
        assertEquals("Test Description", result.getDescription().get());

        verify(repositoryPort, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // Arrange
        Long productId = 999L;
        when(repositoryPort.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> useCase.execute(productId)
        );

        assertEquals("product not found", exception.getMessage());
        verify(repositoryPort, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should get product with empty description when product exists without description")
    void shouldGetProductWithEmptyDescription() {
        // Arrange
        Long productId = 2L;
        Product product = Product.builder()
                .id(productId)
                .name("Test Product Without Description")
                .price(149.99)
                .description(Optional.empty())
                .build();

        when(repositoryPort.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Product result = useCase.execute(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product Without Description", result.getName());
        assertEquals(149.99, result.getPrice());
        assertFalse(result.getDescription().isPresent());

        verify(repositoryPort, times(1)).findById(productId);
    }
}
