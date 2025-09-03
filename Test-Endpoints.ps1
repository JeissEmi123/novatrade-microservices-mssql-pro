# ===========================================================
# NOVATRADE MICROSERVICIOS - COMANDOS CURL PARA TESTING
# ===========================================================
# Este script contiene todos los comandos curl para probar
# los endpoints de los microservicios de Catálogo e Inventario
# ===========================================================

Write-Host "=== PRUEBA DE ENDPOINTS DEL MICROSERVICIO DE CATÁLOGO ===" -ForegroundColor Cyan
Write-Host

# 1. PÁGINA DE INICIO Y API GENERAL
Write-Host "1. Obtener información general (Página de inicio)" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8081/" -Headers @{"Accept"="application/json"} | ConvertTo-Json -Depth 4

Write-Host
Write-Host "2. Obtener información de la API" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8081/api" -Headers @{"Accept"="application/json"} | ConvertTo-Json -Depth 4

Write-Host
# 2. ENDPOINTS DE PRODUCTOS
Write-Host "3. Listar todos los productos" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8081/api/v1/products" -Headers @{"Accept"="application/vnd.api+json"} | ConvertTo-Json -Depth 4

Write-Host
Write-Host "4. Crear un nuevo producto" -ForegroundColor Green
$body1 = @{
    data = @{
        type = "products"
        attributes = @{
            name = "Laptop HP Pavilion"
            price = 899.99
            description = "Laptop de alto rendimiento con procesador i7 y 16GB RAM"
        }
    }
} | ConvertTo-Json -Depth 4
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/products" `
    -Headers @{
        "Content-Type"="application/vnd.api+json"
        "Accept"="application/vnd.api+json"
        "X-API-Key"="catalog-secret"
    } `
    -Body $body1 | ConvertTo-Json -Depth 4

Write-Host
Write-Host "5. Crear un segundo producto" -ForegroundColor Green
$body2 = @{
    data = @{
        type = "products"
        attributes = @{
            name = "Monitor UltraWide 34`""
            price = 449.99
            description = "Monitor curvo de 34 pulgadas con resolución 3440x1440"
        }
    }
} | ConvertTo-Json -Depth 4
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/products" `
    -Headers @{
        "Content-Type"="application/vnd.api+json"
        "Accept"="application/vnd.api+json"
        "X-API-Key"="catalog-secret"
    } `
    -Body $body2 | ConvertTo-Json -Depth 4

Write-Host
Write-Host "6. Obtener producto por ID (sustituye el ID según corresponda)" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8081/api/v1/products/1" -Headers @{"Accept"="application/vnd.api+json"} | ConvertTo-Json -Depth 4

Write-Host
Write-Host "=== PRUEBA DE ENDPOINTS DEL MICROSERVICIO DE INVENTARIO ===" -ForegroundColor Cyan
Write-Host

# 3. ENDPOINTS DE INVENTARIO
Write-Host "7. Consultar inventario de un producto (sustituye el ID según corresponda)" -ForegroundColor Green
try {
    Invoke-RestMethod -Uri "http://localhost:8082/api/v1/inventory/1" `
        -Headers @{
            "Accept"="application/vnd.api+json"
            "X-API-Key"="inventory-secret"
        } | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "El microservicio de inventario podría no estar en ejecución en el puerto 8082" -ForegroundColor Yellow
}

Write-Host
Write-Host "8. Actualizar inventario de un producto" -ForegroundColor Green
$body3 = @{
    data = @{
        type = "inventory"
        id = "1"
        attributes = @{
            quantity = 50
        }
    }
} | ConvertTo-Json -Depth 4
try {
    Invoke-RestMethod -Method Put -Uri "http://localhost:8082/api/v1/inventory/1" `
        -Headers @{
            "Content-Type"="application/vnd.api+json"
            "Accept"="application/vnd.api+json"
            "X-API-Key"="inventory-secret"
        } `
        -Body $body3 | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "El microservicio de inventario podría no estar en ejecución en el puerto 8082" -ForegroundColor Yellow
}

Write-Host
Write-Host "9. Realizar una compra" -ForegroundColor Green
$body4 = @{
    data = @{
        type = "purchase"
        attributes = @{
            productId = 1
            quantity = 2
        }
    }
} | ConvertTo-Json -Depth 4
try {
    Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/v1/purchases" `
        -Headers @{
            "Content-Type"="application/vnd.api+json"
            "Accept"="application/vnd.api+json"
            "X-API-Key"="inventory-secret"
        } `
        -Body $body4 | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "El microservicio de inventario podría no estar en ejecución en el puerto 8082" -ForegroundColor Yellow
}

Write-Host
Write-Host "10. Verificar inventario después de la compra" -ForegroundColor Green
try {
    Invoke-RestMethod -Uri "http://localhost:8082/api/v1/inventory/1" `
        -Headers @{
            "Accept"="application/vnd.api+json"
            "X-API-Key"="inventory-secret"
        } | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "El microservicio de inventario podría no estar en ejecución en el puerto 8082" -ForegroundColor Yellow
}

Write-Host
Write-Host "=== PRUEBAS DE ERRORES Y VALIDACIONES ===" -ForegroundColor Yellow

Write-Host
Write-Host "11. Intentar crear un producto sin nombre (debería fallar)" -ForegroundColor Green
$body5 = @{
    data = @{
        type = "products"
        attributes = @{
            name = ""
            price = 99.99
            description = "Producto sin nombre"
        }
    }
} | ConvertTo-Json -Depth 4
try {
    Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/products" `
        -Headers @{
            "Content-Type"="application/vnd.api+json"
            "Accept"="application/vnd.api+json"
            "X-API-Key"="catalog-secret"
        } `
        -Body $body5 | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error (esperado): $_" -ForegroundColor Yellow
}

Write-Host
Write-Host "12. Intentar crear un producto con precio negativo (debería fallar)" -ForegroundColor Green
$body6 = @{
    data = @{
        type = "products"
        attributes = @{
            name = "Producto con precio negativo"
            price = -10.99
            description = "Este producto tiene un precio inválido"
        }
    }
} | ConvertTo-Json -Depth 4
try {
    Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/products" `
        -Headers @{
            "Content-Type"="application/vnd.api+json"
            "Accept"="application/vnd.api+json"
            "X-API-Key"="catalog-secret"
        } `
        -Body $body6 | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error (esperado): $_" -ForegroundColor Yellow
}

Write-Host
Write-Host "13. Intentar acceder sin API Key (debería fallar)" -ForegroundColor Green
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/v1/products" -Headers @{"Accept"="application/vnd.api+json"} | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error (esperado): $_" -ForegroundColor Yellow
}

Write-Host
Write-Host "14. Intentar una compra con cantidad mayor al inventario (debería fallar)" -ForegroundColor Green
$body7 = @{
    data = @{
        type = "purchase"
        attributes = @{
            productId = 1
            quantity = 999
        }
    }
} | ConvertTo-Json -Depth 4
try {
    Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/v1/purchases" `
        -Headers @{
            "Content-Type"="application/vnd.api+json"
            "Accept"="application/vnd.api+json"
            "X-API-Key"="inventory-secret"
        } `
        -Body $body7 | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error (esperado): $_" -ForegroundColor Yellow
}

Write-Host
Write-Host "15. Intentar acceder a un producto que no existe (debería devolver 404)" -ForegroundColor Green
try {
    Invoke-RestMethod -Uri "http://localhost:8081/api/v1/products/99999" `
        -Headers @{
            "Accept"="application/vnd.api+json"
            "X-API-Key"="catalog-secret"
        } | ConvertTo-Json -Depth 4
} catch {
    Write-Host "Error (esperado): $_" -ForegroundColor Yellow
}

Write-Host
Write-Host "=== FIN DE LAS PRUEBAS ===" -ForegroundColor Cyan
