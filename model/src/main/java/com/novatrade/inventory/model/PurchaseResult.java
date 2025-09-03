package com.novatrade.inventory.model;
import lombok.*;

@Data
@Builder
public class PurchaseResult{
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Long quantity;
    private Double totalPrice; // Cambiado de total a totalPrice
    private boolean success; // Añadido campo para indicar éxito
    private String purchasedAt;
}
