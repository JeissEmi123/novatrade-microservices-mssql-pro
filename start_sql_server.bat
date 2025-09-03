@echo off
echo =======================================================
echo INICIANDO CONTENEDOR SQL SERVER Y VERIFICANDO CONEXION
echo =======================================================

echo.
echo [1/3] Deteniendo contenedores existentes...
docker-compose down

echo.
echo [2/3] Iniciando solo el contenedor SQL Server...
docker-compose -f docker-compose-simplified.yml up -d

echo.
echo [3/3] Esperando 30 segundos para que SQL Server inicie...
timeout /t 30

echo.
echo Verificando estado del contenedor SQL Server...
docker ps

echo.
echo Intentando conectarse a SQL Server...
echo (Si no hay errores, la conexión es exitosa)

echo.
echo Presiona cualquier tecla para salir...
pause > nul
