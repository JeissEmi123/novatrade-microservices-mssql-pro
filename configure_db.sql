
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'catalog')
BEGIN
    CREATE DATABASE catalog;
END
GO

USE catalog;
GO

IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'catalog_user')
BEGIN
    CREATE LOGIN catalog_user WITH PASSWORD = 'Catalogo123!';
END
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'catalog_user')
BEGIN
    CREATE USER catalog_user FOR LOGIN catalog_user;
    ALTER ROLE db_owner ADD MEMBER catalog_user;
END
GO

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND type in (N'U'))
BEGIN
    CREATE TABLE product (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500),
        price DECIMAL(18,2) NOT NULL,
        stock INT NOT NULL DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    INSERT INTO product (name, description, price, stock)
    VALUES
        ('Laptop Dell XPS 13', 'Laptop de alta gama con procesador Intel Core i7', 1299.99, 10),
        ('Smartphone Samsung Galaxy S21', 'Smartphone con cámara de 108MP y pantalla AMOLED', 899.99, 15),
        ('Tablet Apple iPad Pro', 'Tablet con chip M1 y pantalla Retina', 799.99, 8);
END
GO

PRINT 'Configuración de base de datos completada exitosamente';
