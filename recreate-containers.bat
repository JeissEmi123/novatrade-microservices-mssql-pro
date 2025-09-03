@echo off
echo ==============================================
echo RECREACIÓN DE CONTENEDORES DOCKER
echo ==============================================

echo.
echo [1/3] Deteniendo y eliminando contenedores existentes...
cd C:\Users\jeiss\OneDrive\Desktop\novatrade-microservices-mssql-pro
docker-compose down

echo.
echo [2/3] Creando nuevos contenedores...
docker-compose up -d

echo.
echo [3/3] Verificando estado de los contenedores...
timeout /t 5
docker ps -a

echo.
echo ==============================================
echo PROCESO COMPLETADO
echo ==============================================
