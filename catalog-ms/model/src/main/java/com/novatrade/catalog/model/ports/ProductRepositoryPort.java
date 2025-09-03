package com.novatrade.catalog.model.ports;
import com.novatrade.catalog.model.Product; import java.util.*;
public interface ProductRepositoryPort { Product save(Product p); Optional<Product> findById(Long id); List<Product> findAll(); }