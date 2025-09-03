package com.novatrade.inventory.usecase;

import com.novatrade.inventory.model.*;
import com.novatrade.inventory.model.ports.*;
import java.time.Instant;
import java.util.NoSuchElementException;

public class PurchaseUseCase {
  private final InventoryRepositoryPort inventoryRepository;
  private final ProductsClientPort productsClient;
  private final IdempotencyPort idempotencyService;

  public PurchaseUseCase(InventoryRepositoryPort inventoryRepository,
                        ProductsClientPort productsClient,
                        IdempotencyPort idempotencyService) {
    this.inventoryRepository = inventoryRepository;
    this.productsClient = productsClient;
    this.idempotencyService = idempotencyService;
  }

  public PurchaseResult execute(Long productId, Long qty, String idempotencyKey) {
    validateQuantity(qty);
    checkIdempotency(idempotencyKey);

    ProductsClientPort.ProductDTO product = getProductFromCatalog(productId);
    decrementInventory(productId, qty);

    return createPurchaseResult(product, qty);
  }

  private void validateQuantity(Long qty) {
    if (qty == null || qty <= 0) {
      throw new IllegalArgumentException("quantity must be > 0");
    }
  }

  private void checkIdempotency(String idempotencyKey) {
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var cached = idempotencyService.findResponseByKey(idempotencyKey);
      if (cached.isPresent()) {
        throw new IllegalStateException("__IDEMPOTENT_RETURN__" + cached.get());
      }
    }
  }

  private ProductsClientPort.ProductDTO getProductFromCatalog(Long productId) {
    try {
      ProductsClientPort.ProductDTO product = productsClient.getProduct(productId);
      if (product == null) {
        throw new NoSuchElementException("product not found");
      }
      return product;
    } catch (NoSuchElementException e) {
      throw e;
    } catch (Exception e) {
      throw new ServiceCommunicationException("Error communicating with catalog service", e);
    }
  }

  private void decrementInventory(Long productId, Long qty) {
    boolean inventoryUpdated = inventoryRepository.decrementIfEnough(productId, qty);
    if (!inventoryUpdated) {
      throw new InsufficientInventoryException("Insufficient inventory for product " + productId);
    }
  }

  private PurchaseResult createPurchaseResult(ProductsClientPort.ProductDTO product, Long qty) {
    double total = product.price() * qty;
    return PurchaseResult.builder()
        .productId(product.id())
        .productName(product.name())
        .unitPrice(product.price())
        .quantity(qty)
        .totalPrice(total)
        .success(true)
        .purchasedAt(Instant.now().toString())
        .build();
  }

  public static class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String message) {
      super(message);
    }
  }
}
