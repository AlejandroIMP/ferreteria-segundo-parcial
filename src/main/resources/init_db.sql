-- Script de inicialización para SQL Server
-- Ajustado para usar nombres de tablas y columnas que coinciden con los DAOs (plural, snake_case)

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRANSACTION;

-- Eliminar tablas (orden inverso por dependencias)
IF OBJECT_ID('dbo.detalle_ventas', 'U') IS NOT NULL DROP TABLE dbo.detalle_ventas;
IF OBJECT_ID('dbo.ventas', 'U') IS NOT NULL DROP TABLE dbo.ventas;
IF OBJECT_ID('dbo.productos', 'U') IS NOT NULL DROP TABLE dbo.productos;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL DROP TABLE dbo.usuarios;
IF OBJECT_ID('dbo.clientes', 'U') IS NOT NULL DROP TABLE dbo.clientes;
IF OBJECT_ID('dbo.proveedores', 'U') IS NOT NULL DROP TABLE dbo.proveedores;
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL DROP TABLE dbo.roles;
IF OBJECT_ID('dbo.estados', 'U') IS NOT NULL DROP TABLE dbo.estados;

-- Tabla de estados (lookup para Estado enum)
CREATE TABLE dbo.estados (
    codigo NVARCHAR(50) PRIMARY KEY, -- ejemplo: PENDIENTE, COMPLETADA, DISPONIBLE, etc.
    nombre NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(255) NULL
);

-- Tabla roles
CREATE TABLE dbo.roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(100) NOT NULL UNIQUE,
    descripcion NVARCHAR(255) NULL,
    puede_vender BIT NOT NULL DEFAULT 0,
    puede_gestionar_inventario BIT NOT NULL DEFAULT 0,
    puede_gestionar_usuarios BIT NOT NULL DEFAULT 0,
    puede_generar_reportes BIT NOT NULL DEFAULT 0,
    puede_gestionar_clientes BIT NOT NULL DEFAULT 0,
    puede_gestionar_proveedores BIT NOT NULL DEFAULT 0,
    activo BIT NOT NULL DEFAULT 1
);

-- Tabla proveedores
CREATE TABLE dbo.proveedores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(200) NOT NULL,
    razon_social NVARCHAR(200) NULL,
    ruc NVARCHAR(50) NULL UNIQUE,
    telefono NVARCHAR(50) NULL,
    email NVARCHAR(150) NULL,
    direccion NVARCHAR(255) NULL,
    contacto NVARCHAR(150) NULL,
    telefono_contacto NVARCHAR(50) NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    activo BIT NOT NULL DEFAULT 1
);

-- Tabla clientes
CREATE TABLE dbo.clientes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre NVARCHAR(150) NOT NULL,
    apellido NVARCHAR(150) NULL,
    cedula NVARCHAR(50) NULL UNIQUE,
    telefono NVARCHAR(50) NULL,
    email NVARCHAR(150) NULL,
    direccion NVARCHAR(255) NULL,
    fecha_registro DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    activo BIT NOT NULL DEFAULT 1
);

-- Tabla usuarios
CREATE TABLE dbo.usuarios (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    nombre NVARCHAR(150) NULL,
    apellido NVARCHAR(150) NULL,
    email NVARCHAR(150) NULL,
    telefono NVARCHAR(50) NULL,
    rol_id BIGINT NULL,
    fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    ultimo_acceso DATETIME2 NULL,
    activo BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_usuarios_roles FOREIGN KEY (rol_id) REFERENCES dbo.roles(id) ON DELETE SET NULL
);

-- Tabla productos
CREATE TABLE dbo.productos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo NVARCHAR(100) NULL UNIQUE,
    nombre NVARCHAR(200) NOT NULL,
    descripcion NVARCHAR(MAX) NULL,
    categoria NVARCHAR(100) NULL,
    marca NVARCHAR(100) NULL,
    modelo NVARCHAR(100) NULL,
    precio_compra DECIMAL(18,2) NULL,
    precio_venta DECIMAL(18,2) NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    unidad_medida NVARCHAR(50) NULL,
    proveedor_id BIGINT NULL,
    fecha_creacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    fecha_actualizacion DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    activo BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_productos_proveedores FOREIGN KEY (proveedor_id) REFERENCES dbo.proveedores(id) ON DELETE NO ACTION
);

-- Tabla ventas
CREATE TABLE dbo.ventas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    numero_venta NVARCHAR(100) NOT NULL UNIQUE,
    cliente_id BIGINT NULL,
    usuario_id BIGINT NULL,
    fecha_venta DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    subtotal DECIMAL(18,2) NOT NULL DEFAULT 0,
    impuesto DECIMAL(18,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(18,2) NOT NULL DEFAULT 0,
    total DECIMAL(18,2) NOT NULL DEFAULT 0,
    estado NVARCHAR(50) NULL,
    iva DECIMAL(18,2) NULL DEFAULT 0,
    observaciones NVARCHAR(MAX) NULL,
    metodo_pago NVARCHAR(100) NULL,
    CONSTRAINT FK_ventas_clientes FOREIGN KEY (cliente_id) REFERENCES dbo.clientes(id) ON DELETE SET NULL,
    CONSTRAINT FK_ventas_usuarios FOREIGN KEY (usuario_id) REFERENCES dbo.usuarios(id) ON DELETE SET NULL
    -- not forcing FK to estados to keep DAO behavior flexible; estado stored as text
);

-- Tabla detalle_ventas
CREATE TABLE dbo.detalle_ventas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    precio_unitario DECIMAL(18,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
    observaciones NVARCHAR(500) NULL,
    CONSTRAINT FK_detalle_ventas_ventas FOREIGN KEY (venta_id) REFERENCES dbo.ventas(id) ON DELETE CASCADE,
    CONSTRAINT FK_detalle_ventas_productos FOREIGN KEY (producto_id) REFERENCES dbo.productos(id) ON DELETE NO ACTION
);

-- Índices sugeridos
CREATE INDEX IX_productos_nombre ON dbo.productos(nombre);
CREATE INDEX IX_clientes_nombre_apellido ON dbo.clientes(nombre, apellido);
CREATE INDEX IX_ventas_fecha ON dbo.ventas(fecha_venta);

-- Seed: Estados (basado en enum Estado.java)
INSERT INTO dbo.estados (codigo, nombre, descripcion) VALUES
('PENDIENTE', 'Pendiente', 'La venta está en proceso'),
('COMPLETADA', 'Completada', 'La venta se ha completado exitosamente'),
('CANCELADA', 'Cancelada', 'La venta ha sido cancelada'),
('DISPONIBLE', 'Disponible', 'Producto disponible para venta'),
('AGOTADO', 'Agotado', 'Producto sin stock'),
('DESCONTINUADO', 'Descontinuado', 'Producto ya no se maneja'),
('ACTIVO', 'Activo', 'Usuario activo en el sistema'),
('INACTIVO', 'Inactivo', 'Usuario desactivado temporalmente'),
('BLOQUEADO', 'Bloqueado', 'Usuario bloqueado por seguridad'),
('EN_PROCESO', 'En Proceso', 'Operación en curso'),
('FINALIZADO', 'Finalizado', 'Operación completada'),
('ERROR', 'Error', 'Operación con errores');

-- Seed: Roles (basados en los factory methods de Rol.java)
INSERT INTO dbo.roles (nombre, descripcion, puede_vender, puede_gestionar_inventario, puede_gestionar_usuarios, puede_generar_reportes, puede_gestionar_clientes, puede_gestionar_proveedores)
VALUES
('ADMINISTRADOR', 'Acceso completo al sistema - puede realizar todas las operaciones', 1,1,1,1,1,1),
('VENDEDOR', 'Especializado en ventas y atención al cliente', 1,0,0,0,1,0),
('INVENTARIO', 'Especializado en gestión de productos y proveedores', 0,1,0,1,0,1),
('CAJERO', 'Especializado unicamente en realizar ventas', 1,0,0,0,0,0),
('SUPERVISOR', 'Puede supervisar ventas y generar reportes', 1,0,0,1,1,0);

COMMIT TRANSACTION;

PRINT 'Inicialización de base de datos completada.';
