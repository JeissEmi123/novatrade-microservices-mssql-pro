package com.novatrade.catalog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novatrade.catalog.model.Product;
import com.novatrade.catalog.usecase.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductUseCase getProductUseCase;

    @Mock
    private ListProductsUseCase listProductsUseCase;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateProduct() throws Exception {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(19.99)
                .description(Optional.of("Test Description"))
                .build();

        when(createProductUseCase.execute(any(Product.class))).thenReturn(product);

        // Prepare JSON request body
        ProductController.ProductBody requestBody = new ProductController.ProductBody(
                new ProductController.ProductBody.Body(
                        "products",
                        new ProductController.ProductBody.Attributes("Test Product", 19.99, "Test Description")
                )
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType("application/vnd.api+json")
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data.type").value("products"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.attributes.name").value("Test Product"))
                .andExpect(jsonPath("$.data.attributes.price").value(19.99))
                .andExpect(jsonPath("$.data.attributes.description").value("Test Description"));

        verify(createProductUseCase, times(1)).execute(any(Product.class));
    }

    @Test
    void testGetProduct() throws Exception {
        // Arrange
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .price(19.99)
                .description(Optional.of("Test Description"))
                .build();

        when(getProductUseCase.execute(productId)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", productId)
                .accept("application/vnd.api+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data.type").value("products"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.attributes.name").value("Test Product"))
                .andExpect(jsonPath("$.data.attributes.price").value(19.99))
                .andExpect(jsonPath("$.data.attributes.description").value("Test Description"));

        verify(getProductUseCase, times(1)).execute(productId);
    }

    @Test
    void testListProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(
            Product.builder()
                    .id(1L)
                    .name("Product 1")
                    .price(19.99)
                    .description(Optional.of("Description 1"))
                    .build(),
            Product.builder()
                    .id(2L)
                    .name("Product 2")
                    .price(29.99)
                    .description(Optional.of("Description 2"))
                    .build()
        );

        when(listProductsUseCase.execute()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products")
                .accept("application/vnd.api+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.api+json"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].type").value("products"))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].attributes.name").value("Product 1"))
                .andExpect(jsonPath("$.data[1].type").value("products"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].attributes.name").value("Product 2"));

        verify(listProductsUseCase, times(1)).execute();
    }
}
