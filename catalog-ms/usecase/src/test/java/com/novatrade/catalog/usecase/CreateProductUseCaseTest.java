package com.novatrade.catalog.usecase;

import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.model.ports.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort repositoryPort;

    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProductUseCase(repositoryPort);
    }

    @Test
    @DisplayName("Should create a product successfully when valid data is provided")
    void shouldCreateProductSuccessfully() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(99.99)
                .description(Optional.of("Test Description"))
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(99.99)
                .description(Optional.of("Test Description"))
                .build();

        when(repositoryPort.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = useCase.execute(product);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        assertTrue(result.getDescription().isPresent());
        assertEquals("Test Description", result.getDescription().get());

        verify(repositoryPort, times(1)).save(product);
    }

    @Test
    @DisplayName("Should create a product successfully without description")
    void shouldCreateProductSuccessfullyWithoutDescription() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(99.99)
                .description(Optional.empty())
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(99.99)
                .description(Optional.empty())
                .build();

        when(repositoryPort.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = useCase.execute(product);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        assertFalse(result.getDescription().isPresent());

        verify(repositoryPort, times(1)).save(product);
    }

    @Test
    @DisplayName("Should throw exception when product name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        Product product = Product.builder()
                .name(null)
                .price(99.99)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(product));

        assertEquals("name is required", exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        // Arrange
        Product product = Product.builder()
                .name("   ")
                .price(99.99)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(product));

        assertEquals("name is required", exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(product));

        assertEquals("price must be >= 0", exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product price is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(-10.0)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(product));

        assertEquals("price must be >= 0", exception.getMessage());
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should create a product with zero price")
    void shouldCreateProductWithZeroPrice() {
        // Arrange
        Product product = Product.builder()
                .name("Free Product")
                .price(0.0)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Free Product")
                .price(0.0)
                .build();

        when(repositoryPort.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = useCase.execute(product);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Free Product", result.getName());
        assertEquals(0.0, result.getPrice());

        verify(repositoryPort, times(1)).save(product);
    }
}
