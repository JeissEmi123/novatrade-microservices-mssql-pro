package com.novatrade.catalog.adapter.jpa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity @Table(name="product") @Getter @Setter
public class ProductEntity {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(nullable=false) private Double price;
  @Column(columnDefinition="NVARCHAR(MAX)") private String description;
}