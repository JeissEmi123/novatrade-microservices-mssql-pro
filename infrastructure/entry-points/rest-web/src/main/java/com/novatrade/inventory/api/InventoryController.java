package com.novatrade.inventory.api;

import com.novatrade.inventory.model.*;
import com.novatrade.inventory.usecase.*;
import com.novatrade.inventory.model.ports.IdempotencyPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/vnd.api+json")
public class InventoryController {

    private final GetInventoryUseCase get;
    private final SetInventoryUseCase set;
    private final PurchaseUseCase purchase;
    private final IdempotencyPort idempotency;

    public InventoryController(
            GetInventoryUseCase g,
            SetInventoryUseCase s,
            PurchaseUseCase p,
            IdempotencyPort idem) {
        this.get = g;
        this.set = s;
        this.purchase = p;
        this.idempotency = idem;
    }

    @GetMapping("/inventory/{productId}")
    public JsonApi get(@PathVariable Long productId) {
        var i = get.execute(productId);
        return JsonApi.of(Map.of(
                "type", "inventory",
                "id", String.valueOf(i.getProductId()),
                "attributes", Map.of("quantity", i.getQuantity())
        ));
    }

    record PatchBody(Body data) {
        record Body(String type, Attributes attributes) {}
        record Attributes(Long quantity) {}
    }

    @PatchMapping(value = "/inventory/{productId}", consumes = "application/vnd.api+json")
    public JsonApi patch(@PathVariable Long productId, @RequestBody PatchBody body) {
        var qty = body.data().attributes().quantity();
        var i = set.execute(productId, qty);

        return JsonApi.of(Map.of(
                "type", "inventory",
                "id", String.valueOf(i.getProductId()),
                "attributes", Map.of("quantity", i.getQuantity())
        ));
    }

    record PurchaseBody(Body data) {
        record Body(String type, Attributes attributes) {}
        record Attributes(Long productId, Long quantity) {}
    }

    @PostMapping(value = "/purchases", consumes = "application/vnd.api+json")
    public ResponseEntity<String> purchase(
            @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
            @RequestBody PurchaseBody body) {

        var a = body.data().attributes();

        if (idemKey != null && !idemKey.isBlank()) {
            var cached = idempotency.findResponseByKey(idemKey);
            if (cached.isPresent()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("application/vnd.api+json"))
                        .body(cached.get());
            }
        }

        var r = purchase.execute(a.productId(), a.quantity(), idemKey);

        var mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().build();
        var rootNode = mapper.createObjectNode();
        var dataNode = rootNode.putObject("data");
        dataNode.put("type", "purchases");
        dataNode.put("id", "venta-" + r.getProductId() + "-" + System.currentTimeMillis());

        var attributesNode = dataNode.putObject("attributes");
        attributesNode.put("productName", r.getProductName());
        attributesNode.put("unitPrice", r.getUnitPrice());
        attributesNode.put("quantity", r.getQuantity());
        attributesNode.put("total", r.getTotalPrice());  // Cambiado de getTotal() a getTotalPrice()
        attributesNode.put("purchasedAt", r.getPurchasedAt());

        var json = rootNode.toString();

        if (idemKey != null && !idemKey.isBlank()) {
            idempotency.saveResponse(idemKey, json);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.api+json"))
                .body(json);
    }
}