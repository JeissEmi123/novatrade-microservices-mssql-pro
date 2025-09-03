IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'inventory' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
  CREATE TABLE dbo.inventory (
    product_id BIGINT NOT NULL PRIMARY KEY,
    quantity BIGINT NOT NULL CHECK (quantity >= 0),
    version BIGINT NULL
  );
END;

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'idempotency_keys' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
  CREATE TABLE dbo.idempotency_keys (
    id NVARCHAR(100) NOT NULL PRIMARY KEY,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    response_json NVARCHAR(MAX) NOT NULL
  );
END;
