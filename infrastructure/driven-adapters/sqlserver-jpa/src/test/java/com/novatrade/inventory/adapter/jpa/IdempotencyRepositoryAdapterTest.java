package com.novatrade.inventory.adapter.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para IdempotencyRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
public class IdempotencyRepositoryAdapterTest {

    private static final String TEST_KEY = "test-idempotency-key-123";
    private static final String TEST_RESPONSE = "{\"result\":\"success\",\"data\":{\"id\":1}}";

    @Mock
    private IdempotencyJpaRepository repository;

    private IdempotencyRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new IdempotencyRepositoryAdapter(repository);
    }

    @Test
    void findResponseByKeyShouldReturnEmptyWhenKeyNotFound() {
        // Arrange
        when(repository.findById(TEST_KEY)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = adapter.findResponseByKey(TEST_KEY);

        // Assert
        assertTrue(result.isEmpty());
        verify(repository).findById(TEST_KEY);
    }

    @Test
    void findResponseByKeyShouldReturnResponseWhenKeyExists() {
        // Arrange
        IdempotencyEntity entity = new IdempotencyEntity(TEST_KEY, TEST_RESPONSE);
        when(repository.findById(TEST_KEY)).thenReturn(Optional.of(entity));

        // Act
        Optional<String> result = adapter.findResponseByKey(TEST_KEY);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(TEST_RESPONSE, result.get());
        verify(repository).findById(TEST_KEY);
    }

    @Test
    void saveResponseShouldCreateAndSaveEntity() {
        // Arrange
        when(repository.save(any(IdempotencyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adapter.saveResponse(TEST_KEY, TEST_RESPONSE);

        // Assert
        verify(repository).save(argThat(entity ->
            entity.getId().equals(TEST_KEY) &&
            entity.getResponseJson().equals(TEST_RESPONSE) &&
            entity.getCreatedAt() != null
        ));
    }
}
