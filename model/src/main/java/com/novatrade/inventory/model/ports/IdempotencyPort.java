package com.novatrade.inventory.model.ports;
import java.util.Optional;
public interface IdempotencyPort { Optional<String> findResponseByKey(String key); void saveResponse(String key, String responseJson); }
