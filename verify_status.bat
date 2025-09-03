@echo off
echo =============================================
echo VERIFICADOR DE CONECTIVIDAD SQL SERVER
echo =============================================
echo.

echo [1/4] Verificando contenedores Docker en ejecución...
docker ps --filter "name=mssql-dev"

echo.
echo [2/4] Verificando estado del contenedor SQL Server...
docker inspect --format="{{.State.Status}}" mssql-dev

echo.
echo [3/4] Verificando bases de datos disponibles...
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "SELECT name FROM sys.databases;"

echo.
echo [4/4] Verificando tabla product en la base de datos...
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "USE master; IF OBJECT_ID('product') IS NOT NULL SELECT COUNT(*) AS 'Número de productos' FROM product; ELSE PRINT 'La tabla product no existe en master';"
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "USE catalog; IF OBJECT_ID('product') IS NOT NULL SELECT COUNT(*) AS 'Número de productos' FROM product; ELSE PRINT 'La tabla product no existe en catalog';"

echo.
echo =============================================
echo DIAGNÓSTICO COMPLETADO
echo =============================================
echo.
echo Ahora puedes acceder a tu API REST en: http://localhost:8081
echo.

pause
