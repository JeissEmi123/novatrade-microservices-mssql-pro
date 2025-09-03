package com.novatrade.inventory;
import com.novatrade.inventory.model.ports.*; import com.novatrade.inventory.usecase.*; import org.springframework.boot.*; import org.springframework.boot.autoconfigure.*; import org.springframework.context.annotation.*;
@SpringBootApplication public class Application {
  public static void main(String[] args){ SpringApplication.run(Application.class,args); }
  @Bean public GetInventoryUseCase get(InventoryRepositoryPort repo){ return new GetInventoryUseCase(repo); }
  @Bean public SetInventoryUseCase set(InventoryRepositoryPort repo){ return new SetInventoryUseCase(repo); }
  @Bean public PurchaseUseCase purchase(InventoryRepositoryPort repo, ProductsClientPort client, IdempotencyPort idem){ return new PurchaseUseCase(repo, client, idem); }
}
