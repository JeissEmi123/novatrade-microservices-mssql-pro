
echo.
echo Intentando conexión directa a SQL Server...
docker exec -i mssql-dev /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong!Passw0rd -Q "SELECT @@VERSION AS 'SQL Server Version'"

echo.
echo Si ves la versión de SQL Server arriba, la conexión interna funciona.
echo Ahora verificando conexión desde host al puerto 11433...

echo.
pause
