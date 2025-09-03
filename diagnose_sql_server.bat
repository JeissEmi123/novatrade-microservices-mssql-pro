@echo off
echo ===========================================================
echo DIAGNÓSTICO COMPLETO DE CONEXIÓN A SQL SERVER
echo ===========================================================

echo.
echo [1/7] Verificando el estado de Docker...
docker info > nul 2>&1
if %errorlevel% neq 0 (
  echo ERROR: Docker no está en ejecución o no está instalado.
  echo Por favor, asegúrate de que Docker Desktop esté iniciado.
  goto :end
)

echo Docker está en ejecución correctamente.

echo.
echo [2/7] Verificando contenedores Docker en ejecución...
docker ps

echo.
echo [3/7] Verificando todos los contenedores (incluso los detenidos)...
docker ps -a

echo.
echo [4/7] Deteniendo y eliminando contenedores existentes...
docker-compose down

echo.
echo [5/7] Iniciando contenedores con la configuración simplificada...
docker-compose -f docker-compose-simplified.yml up -d

echo.
echo [6/7] Esperando a que SQL Server inicie completamente (30 segundos)...
timeout /t 30

echo.
echo [7/7] Verificando si el contenedor de SQL Server responde...
FOR /F "tokens=* USEBACKQ" %%F IN (`docker ps --filter "name=mssql" --format "{{.Names}}"`) DO (
  SET CONTAINER_NAME=%%F
  echo SQL Server encontrado: %%F

  echo.
  echo Verificando logs del contenedor...
  docker logs %%F

  echo.
  echo Intentando conectarse a SQL Server con SA...
  docker exec -i %%F /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "SELECT @@VERSION AS 'SQL Server Version';"

  if errorlevel 1 (
    echo.
    echo ERROR: No se pudo conectar a SQL Server. Verificando puertos...
    docker port %%F

    echo.
    echo Verificando estado del contenedor:
    docker inspect --format='{{.State.Status}}' %%F

    echo.
    echo Últimas 20 líneas de logs del contenedor:
    docker logs --tail 20 %%F
  ) else (
    echo.
    echo ÉXITO: Conexión a SQL Server establecida.
    echo.
    echo Verificando bases de datos disponibles:
    docker exec -i %%F /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "SELECT name FROM sys.databases;"

    echo.
    echo Verificando la conectividad desde fuera del contenedor:
    echo Conexión local al puerto 11433:
    docker exec -i %%F /opt/mssql-tools/bin/sqlcmd -S host.docker.internal,11433 -U sa -P YourStrong!Passw0rd -Q "SELECT @@VERSION AS 'SQL Server Version';"
  )
)

:end
echo.
echo ===========================================================
echo DIAGNÓSTICO COMPLETADO
echo ===========================================================

pause
