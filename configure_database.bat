@echo off
echo =========================================
echo Configurando la base de datos SQL Server
echo =========================================

echo Obteniendo el nombre del contenedor de SQL Server...
FOR /F "tokens=* USEBACKQ" %%F IN (`docker ps --filter "name=mssql" --format "{{.Names}}"`) DO (
  SET CONTAINER_NAME=%%F
)

echo Copiando el archivo SQL al contenedor...
docker cp configure_db.sql %CONTAINER_NAME%:/tmp/configure_db.sql

echo Ejecutando el script SQL en el contenedor...
docker exec -i %CONTAINER_NAME% /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -i /tmp/configure_db.sql

echo =========================================
echo Configuración completada
echo =========================================
