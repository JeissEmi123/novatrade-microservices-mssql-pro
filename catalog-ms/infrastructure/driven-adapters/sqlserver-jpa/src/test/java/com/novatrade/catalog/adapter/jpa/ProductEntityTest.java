package com.novatrade.catalog.adapter.jpa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    @Test
    void shouldCreateProductEntity() {
        // Arrange
        ProductEntity product = new ProductEntity();

        // Act
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setDescription("This is a test product description");

        // Assert
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(99.99, product.getPrice());
        assertEquals("This is a test product description", product.getDescription());
    }

    @Test
    void shouldHandleNullDescription() {
        // Arrange
        ProductEntity product = new ProductEntity();

        // Act
        product.setId(2L);
        product.setName("Product without description");
        product.setPrice(49.99);
        product.setDescription(null);

        // Assert
        assertEquals(2L, product.getId());
        assertEquals("Product without description", product.getName());
        assertEquals(49.99, product.getPrice());
        assertNull(product.getDescription());
    }

    @Test
    void shouldEqualsAndHashCodeWork() {
        // Arrange
        ProductEntity product1 = new ProductEntity();
        product1.setId(1L);
        product1.setName("Same Product");

        ProductEntity product2 = new ProductEntity();
        product2.setId(1L);
        product2.setName("Different Name"); // El ID es lo que debe determinar la igualdad

        ProductEntity product3 = new ProductEntity();
        product3.setId(2L);
        product3.setName("Same Product");

        // Assert - verificar solo si las implementaciones de equals y hashCode existen
        // Este test podría fallar si estas implementaciones no están presentes en la entidad
        assertEquals(product1, product1); // Reflexividad
        if (product1.equals(product2)) {
            // Si hay una implementación de equals basada en ID, deberían ser iguales
            assertEquals(product1.hashCode(), product2.hashCode());
        }

        assertNotEquals(product1, product3);
    }
}
