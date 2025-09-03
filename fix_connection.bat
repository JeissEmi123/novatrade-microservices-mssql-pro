@echo off
echo ========================================
echo INICIANDO SQL SERVER PARA DESARROLLO
echo ========================================

echo.
echo [1/4] Deteniendo contenedores previos...
docker-compose down

echo.
echo [2/4] Iniciando SQL Server...
docker run -d --name mssql-dev -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=YourStrong!Passw0rd" -p 11433:1433 mcr.microsoft.com/mssql/server:2022-latest

echo.
echo [3/4] Esperando a que SQL Server inicie (15 segundos)...
timeout /t 15 > nul

echo.
echo [4/4] Creando bases de datos requeridas...
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "IF DB_ID('catalog') IS NULL CREATE DATABASE catalog; IF DB_ID('inventory') IS NULL CREATE DATABASE inventory;"

echo.
echo Creando usuarios necesarios...
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -d catalog -Q "IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'catalog_user') BEGIN CREATE LOGIN catalog_user WITH PASSWORD = 'Catalogo123!'; END; IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'catalog_user') BEGIN CREATE USER catalog_user FOR LOGIN catalog_user; ALTER ROLE db_owner ADD MEMBER catalog_user; END;"

echo.
echo ========================================
echo CONFIGURACIÓN COMPLETADA
echo ========================================
echo SQL Server está en ejecución en el puerto 11433
echo Nombre de usuario: sa
echo Contraseña: YourStrong!Passw0rd
echo ========================================

echo.
echo Presiona cualquier tecla para continuar...
pause > nul
