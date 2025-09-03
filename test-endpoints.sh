#!/bin/bash
# =========================================================
# NOVATRADE MICROSERVICIOS - COMANDOS CURL PARA TESTING
# =========================================================
# Este script contiene todos los comandos curl para probar
# los endpoints de los microservicios de Catálogo e Inventario
# =========================================================

echo "=== PRUEBA DE ENDPOINTS DEL MICROSERVICIO DE CATÁLOGO ==="
echo

# 1. PÁGINA DE INICIO Y API GENERAL
echo "1. Obtener información general (Página de inicio)"
curl -X GET http://localhost:8081/ -H "Accept: application/json" | jq .

echo
echo "2. Obtener información de la API"
curl -X GET http://localhost:8081/api -H "Accept: application/json" | jq .

echo
# 2. ENDPOINTS DE PRODUCTOS
echo "3. Listar todos los productos"
curl -X GET http://localhost:8081/api/v1/products -H "Accept: application/vnd.api+json" | jq .

echo
echo "4. Crear un nuevo producto"
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: catalog-secret" \
  -d '{
    "data": {
      "type": "products",
      "attributes": {
        "name": "Laptop HP Pavilion",
        "price": 899.99,
        "description": "Laptop de alto rendimiento con procesador i7 y 16GB RAM"
      }
    }
  }' | jq .

echo
echo "5. Crear un segundo producto"
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: catalog-secret" \
  -d '{
    "data": {
      "type": "products",
      "attributes": {
        "name": "Monitor UltraWide 34\"",
        "price": 449.99,
        "description": "Monitor curvo de 34 pulgadas con resolución 3440x1440"
      }
    }
  }' | jq .

echo
echo "6. Obtener producto por ID (sustituye el ID según corresponda)"
curl -X GET http://localhost:8081/api/v1/products/1 -H "Accept: application/vnd.api+json" | jq .

echo
echo "=== PRUEBA DE ENDPOINTS DEL MICROSERVICIO DE INVENTARIO ==="
echo

# 3. ENDPOINTS DE INVENTARIO
echo "7. Consultar inventario de un producto (sustituye el ID según corresponda)"
curl -X GET http://localhost:8082/api/v1/inventory/1 \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: inventory-secret" | jq .

echo
echo "8. Actualizar inventario de un producto"
curl -X PUT http://localhost:8082/api/v1/inventory/1 \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: inventory-secret" \
  -d '{
    "data": {
      "type": "inventory",
      "id": "1",
      "attributes": {
        "quantity": 50
      }
    }
  }' | jq .

echo
echo "9. Realizar una compra"
curl -X POST http://localhost:8082/api/v1/purchases \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: inventory-secret" \
  -d '{
    "data": {
      "type": "purchase",
      "attributes": {
        "productId": 1,
        "quantity": 2
      }
    }
  }' | jq .

echo
echo "10. Verificar inventario después de la compra"
curl -X GET http://localhost:8082/api/v1/inventory/1 \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: inventory-secret" | jq .

echo
echo "=== PRUEBAS DE ERRORES Y VALIDACIONES ==="

echo
echo "11. Intentar crear un producto sin nombre (debería fallar)"
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: catalog-secret" \
  -d '{
    "data": {
      "type": "products",
      "attributes": {
        "name": "",
        "price": 99.99,
        "description": "Producto sin nombre"
      }
    }
  }' | jq .

echo
echo "12. Intentar crear un producto con precio negativo (debería fallar)"
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: catalog-secret" \
  -d '{
    "data": {
      "type": "products",
      "attributes": {
        "name": "Producto con precio negativo",
        "price": -10.99,
        "description": "Este producto tiene un precio inválido"
      }
    }
  }' | jq .

echo
echo "13. Intentar acceder sin API Key (debería fallar)"
curl -X GET http://localhost:8081/api/v1/products \
  -H "Accept: application/vnd.api+json" | jq .

echo
echo "14. Intentar una compra con cantidad mayor al inventario (debería fallar)"
curl -X POST http://localhost:8082/api/v1/purchases \
  -H "Content-Type: application/vnd.api+json" \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: inventory-secret" \
  -d '{
    "data": {
      "type": "purchase",
      "attributes": {
        "productId": 1,
        "quantity": 999
      }
    }
  }' | jq .

echo
echo "15. Intentar acceder a un producto que no existe (debería devolver 404)"
curl -X GET http://localhost:8081/api/v1/products/99999 \
  -H "Accept: application/vnd.api+json" \
  -H "X-API-Key: catalog-secret" | jq .

echo
echo "=== FIN DE LAS PRUEBAS ==="
