@echo off
echo ==========================================
echo VERIFICACIÓN DE CONEXIÓN A SQL SERVER
echo ==========================================

echo.
echo [1/3] Verificando contenedores Docker en ejecución...
docker ps

echo.
echo [2/3] Verificando si SQL Server está en ejecución...
FOR /F "tokens=* USEBACKQ" %%F IN (`docker ps --filter "name=mssql" --format "{{.Names}}"`) DO (
  SET CONTAINER_NAME=%%F
  echo SQL Server encontrado: %%F
)

if not defined CONTAINER_NAME (
  echo No se encontró ningún contenedor de SQL Server en ejecución.
  echo Por favor, asegúrate de que el contenedor esté iniciado.
  echo Ejecutando recreate-containers.bat...
  call recreate-containers.bat
  timeout /t 10
  FOR /F "tokens=* USEBACKQ" %%F IN (`docker ps --filter "name=mssql" --format "{{.Names}}"`) DO (
    SET CONTAINER_NAME=%%F
    echo SQL Server encontrado después de recrear: %%F
  )
)

echo.
echo [3/3] Verificando la conexión a la base de datos y al usuario catalog_user...
echo.
echo -- Primero, verificando con el usuario SA...
echo SELECT 'SQL Server está en funcionamiento' AS estado; > test_sa.sql
docker cp test_sa.sql %CONTAINER_NAME%:/tmp/test_sa.sql
docker exec -i %CONTAINER_NAME% /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -i /tmp/test_sa.sql

echo.
echo -- Verificando si existe la base de datos catalog...
echo SELECT name, state_desc FROM sys.databases WHERE name = 'catalog'; > test_db.sql
docker cp test_db.sql %CONTAINER_NAME%:/tmp/test_db.sql
docker exec -i %CONTAINER_NAME% /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -i /tmp/test_db.sql

echo.
echo -- Ejecutando script de configuración de base de datos...
docker cp configure_db.sql %CONTAINER_NAME%:/tmp/configure_db.sql
docker exec -i %CONTAINER_NAME% /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -i /tmp/configure_db.sql

echo.
echo -- Verificando la conexión con usuario catalog_user...
echo SELECT 'Conexión exitosa con catalog_user' AS estado; > test_catalog_user.sql
docker cp test_catalog_user.sql %CONTAINER_NAME%:/tmp/test_catalog_user.sql
docker exec -i %CONTAINER_NAME% /opt/mssql-tools/bin/sqlcmd -S localhost -U catalog_user -P Catalogo123! -d catalog -i /tmp/test_catalog_user.sql

echo.
echo ==========================================
echo Fin de la verificación
echo ==========================================
