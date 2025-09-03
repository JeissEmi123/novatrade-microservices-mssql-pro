package com.novatrade.catalog.model;
import lombok.*; import java.util.Optional;
@Data @Builder public class Product { private Long id; private String name; private Double price; private Optional<String> description; }