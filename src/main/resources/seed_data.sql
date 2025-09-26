-- Script de seed: inserta datos de ejemplo en tablas (excepto roles y estados)
-- Orden: proveedores -> clientes -> usuarios -> productos -> ventas -> detalle_ventas

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

-- Insertar proveedores si no existen
IF NOT EXISTS (SELECT 1 FROM dbo.proveedores)
BEGIN
    INSERT INTO dbo.proveedores (nombre, razon_social, ruc, telefono, email, direccion, contacto, telefono_contacto)
    VALUES
    ('Proveedor A', 'Proveedor A S.A.', 'RUC0001', '0999000001', 'contacto@proveedorA.com', 'Av. Principal 123', 'Juan Perez', '099900111'),
    ('Proveedor B', 'Proveedor B SRL', 'RUC0002', '0999000002', 'ventas@proveedorB.com', 'Calle Secundaria 45', 'María Lopez', '099900222'),
    ('Proveedor C', 'Proveedor C EIRL', 'RUC0003', '0999000003', 'info@proveedorC.com', 'Zona Industrial 9', 'Carlos Gómez', '099900333');
END

-- Insertar clientes si no existen
IF NOT EXISTS (SELECT 1 FROM dbo.clientes)
BEGIN
    INSERT INTO dbo.clientes (nombre, apellido, cedula, telefono, email, direccion)
    VALUES
    ('Luis', 'Fernández', 'C-1001', '098700111', 'luis.fer@example.com', 'Calle 10 #123'),
    ('Ana', 'Martínez', 'C-1002', '098700222', 'ana.m@example.com', 'Calle 20 #456'),
    ('Pedro', 'García', 'C-1003', '098700333', 'pedro.g@example.com', 'Calle 30 #789'),
    ('María', 'Santos', 'C-1004', '098700444', 'maria.s@example.com', 'Calle 40 #321');
END

-- Insertar usuarios si no existen (usa roles ya creados)
IF NOT EXISTS (SELECT 1 FROM dbo.usuarios)
BEGIN
    -- Passwords de ejemplo (hash SHA-256 de "admin123" usado para admin en DatabaseManager)
    DECLARE @hash_admin NVARCHAR(255) = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9';
    DECLARE @hash_user NVARCHAR(255) = '5e884898da28047151d0e56f8dc6292773603d0d6aabbddc8a1b9d6f7e0f2b5b'; -- ejemplo (sha256 'password')

    INSERT INTO dbo.usuarios (username, password, nombre, apellido, email, telefono, rol_id)
    VALUES
    ('admin', @hash_admin, 'Administrador', 'Sistema', 'admin@ferreteria.com', '0999000000', (SELECT id FROM dbo.roles WHERE nombre = 'ADMINISTRADOR')),
    ('vendedor1', @hash_user, 'Carlos', 'Vendedor', 'carlos.v@example.com', '099900444', (SELECT id FROM dbo.roles WHERE nombre = 'VENDEDOR'));
END

-- Insertar productos si no existen
IF NOT EXISTS (SELECT 1 FROM dbo.productos)
BEGIN
    INSERT INTO dbo.productos (codigo, nombre, descripcion, categoria, marca, modelo, precio_compra, precio_venta, stock, stock_minimo, unidad_medida, proveedor_id)
    VALUES
    ('P-1001', 'Tornillo 3cm', 'Tornillo acero zincado', 'Ferretería', 'MarcaX', 'TX-3', 0.05, 0.10, 1000, 50, 'unidad', (SELECT id FROM dbo.proveedores WHERE nombre = 'Proveedor A')),
    ('P-1002', 'Taladro 500W', 'Taladro eléctrico 500W', 'Herramientas', 'DrillPro', 'DP-500', 25.00, 45.00, 50, 2, 'unidad', (SELECT id FROM dbo.proveedores WHERE nombre = 'Proveedor B')),
    ('P-1003', 'Cemento 50kg', 'Cemento gris bolsa 50kg', 'Construcción', 'CemCorp', 'CC-50', 6.00, 10.00, 200, 10, 'bolsa', (SELECT id FROM dbo.proveedores WHERE nombre = 'Proveedor C'));
END

-- Insertar ventas de ejemplo si no existen
IF NOT EXISTS (SELECT 1 FROM dbo.ventas)
BEGIN
    INSERT INTO dbo.ventas (numero_venta, cliente_id, usuario_id, subtotal, impuesto, descuento, total, estado, iva, observaciones, metodo_pago)
    VALUES
    ('V-2025-0001', (SELECT id FROM dbo.clientes WHERE cedula = 'C-1001'), (SELECT id FROM dbo.usuarios WHERE username = 'vendedor1'), 10.00, 1.80, 0.00, 11.80, 'COMPLETADA', 0.18, 'Venta presencial', 'EFECTIVO'),
    ('V-2025-0002', (SELECT id FROM dbo.clientes WHERE cedula = 'C-1002'), (SELECT id FROM dbo.usuarios WHERE username = 'vendedor1'), 45.00, 8.10, 0.00, 53.10, 'COMPLETADA', 0.18, 'Venta con tarjeta', 'TARJETA');
END

-- Insertar detalles de ventas (asociados a las ventas anteriores) si no existen
IF NOT EXISTS (SELECT 1 FROM dbo.detalle_ventas)
BEGIN
    -- Detalle para V-2025-0001
    INSERT INTO dbo.detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal, observaciones)
    VALUES
    ((SELECT id FROM dbo.ventas WHERE numero_venta = 'V-2025-0001'), (SELECT id FROM dbo.productos WHERE codigo = 'P-1001'), 100, 0.10, 10.00, 'Paquete de tornillos'),

    -- Detalle para V-2025-0002
    ((SELECT id FROM dbo.ventas WHERE numero_venta = 'V-2025-0002'), (SELECT id FROM dbo.productos WHERE codigo = 'P-1002'), 1, 45.00, 45.00, 'Taladro modelo DP-500');
END

COMMIT TRANSACTION;

PRINT 'Seed de datos completado.';

