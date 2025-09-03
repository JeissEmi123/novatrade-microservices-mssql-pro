package com.novatrade.catalog.adapter.jpa;

import com.novatrade.catalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    private ProductEntity productEntity;
    private Product product;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        productEntity = new ProductEntity();
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setPrice(29.99);
        productEntity.setDescription("Test Description");

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(29.99)
                .description(Optional.of("Test Description"))
                .build();
    }

    @Test
    void save_ShouldSaveProductAndReturnDomainObject() {
        // Arrange
        when(repository.save(any(ProductEntity.class))).thenReturn(productEntity);

        // Act
        Product savedProduct = adapter.save(product);

        // Assert
        assertNotNull(savedProduct);
        assertEquals(product.getId(), savedProduct.getId());
        assertEquals(product.getName(), savedProduct.getName());
        assertEquals(product.getPrice(), savedProduct.getPrice());
        assertEquals(product.getDescription(), savedProduct.getDescription());

        verify(repository).save(any(ProductEntity.class));
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(productEntity));

        // Act
        Optional<Product> foundProduct = adapter.findById(1L);

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getId(), foundProduct.get().getId());
        assertEquals(product.getName(), foundProduct.get().getName());
        assertEquals(product.getPrice(), foundProduct.get().getPrice());
        assertEquals(product.getDescription(), foundProduct.get().getDescription());

        verify(repository).findById(1L);
    }

    @Test
    void findById_WhenProductDoesNotExist_ShouldReturnEmptyOptional() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> foundProduct = adapter.findById(99L);

        // Assert
        assertFalse(foundProduct.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        ProductEntity anotherProductEntity = new ProductEntity();
        anotherProductEntity.setId(2L);
        anotherProductEntity.setName("Another Product");
        anotherProductEntity.setPrice(49.99);
        anotherProductEntity.setDescription("Another Description");

        when(repository.findAll()).thenReturn(Arrays.asList(productEntity, anotherProductEntity));

        // Act
        List<Product> products = adapter.findAll();

        // Assert
        assertEquals(2, products.size());

        // Verificar el primer producto
        assertEquals(productEntity.getId(), products.get(0).getId());
        assertEquals(productEntity.getName(), products.get(0).getName());
        assertEquals(productEntity.getPrice(), products.get(0).getPrice());
        // Corregido: Extraemos el valor del Optional antes de comparar
        assertEquals(productEntity.getDescription(), products.get(0).getDescription().orElse(null));

        // Verificar el segundo producto
        assertEquals(anotherProductEntity.getId(), products.get(1).getId());
        assertEquals(anotherProductEntity.getName(), products.get(1).getName());
        assertEquals(anotherProductEntity.getPrice(), products.get(1).getPrice());
        // Corregido: Extraemos el valor del Optional antes de comparar
        assertEquals(anotherProductEntity.getDescription(), products.get(1).getDescription().orElse(null));

        verify(repository).findAll();
    }

    @Test
    void toEntity_WithNullDescription_ShouldHandleNullDescription() {
        // Arrange
        Product productWithNullDescription = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(29.99)
                .description(Optional.empty())
                .build();

        when(repository.save(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity savedEntity = invocation.getArgument(0);
            assertNull(savedEntity.getDescription());
            return savedEntity;
        });

        // Act
        adapter.save(productWithNullDescription);

        // Assert
        verify(repository).save(any(ProductEntity.class));
    }
}
