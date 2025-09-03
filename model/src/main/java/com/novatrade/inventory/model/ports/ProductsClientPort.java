package com.novatrade.inventory.model.ports;
public interface ProductsClientPort { record ProductDTO(Long id, String name, Double price){} ProductDTO getProduct(Long id); }