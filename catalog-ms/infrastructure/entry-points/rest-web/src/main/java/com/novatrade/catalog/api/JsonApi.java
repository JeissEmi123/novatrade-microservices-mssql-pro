package com.novatrade.catalog.api;

import java.util.Map;

public record JsonApi(Object data, Map<String, Object> meta) {
    public static JsonApi of(Object data) {
        return new JsonApi(data, null);
    }
}

