package com.ferreteriagd.sistemaFerreteria.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Administrador de conexiones a la base de datos SQL Server
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseConfig config;

    private DatabaseManager() {
        this.config = new DatabaseConfig();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtiene una conexión a la base de datos
     */
    public Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection connection;
            if (config.useIntegratedSecurity()) {
                // Usar autenticación de Windows
                connection = DriverManager.getConnection(config.getConnectionString());
            } else {
                // Usar usuario y contraseña
                connection = DriverManager.getConnection(
                    config.getConnectionString(),
                    config.getUsername(),
                    config.getPassword()
                );
            }

            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQL Server no encontrado", e);
        }
    }

    /**
     * Verifica si la conexión a la base de datos es exitosa
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra recursos de manera segura
     */
    public void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    /**
     * Cierra recursos de manera segura (sin ResultSet)
     */
    public void closeResources(Connection conn, PreparedStatement stmt) {
        closeResources(conn, stmt, null);
    }

    /**
     * Inicializa la base de datos creando las tablas necesarias
     */
    public boolean initializeDatabase() {
        try (Connection conn = getConnection()) {
            System.out.println("Inicializando base de datos...");

            // Crear las tablas en orden de dependencia
            createRolesTable(conn);
            createUsuariosTable(conn);
            createProveedoresTable(conn);
            createProductosTable(conn);
            createClientesTable(conn);
            createVentasTable(conn);
            createDetalleVentasTable(conn);

            // Insertar datos iniciales
            insertInitialData(conn);

            System.out.println("Base de datos inicializada correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al inicializar base de datos: " + e.getMessage());
            return false;
        }
    }

    private void createRolesTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='roles' AND xtype='U')
            CREATE TABLE roles (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                nombre NVARCHAR(50) NOT NULL UNIQUE,
                descripcion NVARCHAR(200),
                puede_vender BIT DEFAULT 0,
                puede_gestionar_inventario BIT DEFAULT 0,
                puede_gestionar_usuarios BIT DEFAULT 0,
                puede_generar_reportes BIT DEFAULT 0,
                puede_gestionar_clientes BIT DEFAULT 0,
                puede_gestionar_proveedores BIT DEFAULT 0,
                activo BIT DEFAULT 1,
                fecha_creacion DATETIME2 DEFAULT GETDATE()
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createUsuariosTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='usuarios' AND xtype='U')
            CREATE TABLE usuarios (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                username NVARCHAR(50) NOT NULL UNIQUE,
                password NVARCHAR(255) NOT NULL,
                nombre NVARCHAR(100) NOT NULL,
                apellido NVARCHAR(100) NOT NULL,
                email NVARCHAR(100),
                telefono NVARCHAR(20),
                rol_id BIGINT NOT NULL,
                fecha_creacion DATETIME2 DEFAULT GETDATE(),
                ultimo_acceso DATETIME2,
                activo BIT DEFAULT 1,
                FOREIGN KEY (rol_id) REFERENCES roles(id)
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createProveedoresTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='proveedores' AND xtype='U')
            CREATE TABLE proveedores (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                nombre NVARCHAR(100) NOT NULL,
                razon_social NVARCHAR(150) NOT NULL,
                ruc NVARCHAR(20) NOT NULL UNIQUE,
                telefono NVARCHAR(20),
                email NVARCHAR(100),
                direccion NVARCHAR(200),
                contacto NVARCHAR(100),
                telefono_contacto NVARCHAR(20),
                fecha_registro DATETIME2 DEFAULT GETDATE(),
                activo BIT DEFAULT 1
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createProductosTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='productos' AND xtype='U')
            CREATE TABLE productos (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                codigo NVARCHAR(50) NOT NULL UNIQUE,
                nombre NVARCHAR(100) NOT NULL,
                descripcion NVARCHAR(300),
                categoria NVARCHAR(50),
                marca NVARCHAR(50),
                modelo NVARCHAR(50),
                precio_compra DECIMAL(10,2),
                precio_venta DECIMAL(10,2) NOT NULL,
                stock INT DEFAULT 0,
                stock_minimo INT DEFAULT 0,
                unidad_medida NVARCHAR(20),
                proveedor_id BIGINT,
                fecha_creacion DATETIME2 DEFAULT GETDATE(),
                fecha_actualizacion DATETIME2 DEFAULT GETDATE(),
                activo BIT DEFAULT 1,
                FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createClientesTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='clientes' AND xtype='U')
            CREATE TABLE clientes (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                nombre NVARCHAR(100) NOT NULL,
                apellido NVARCHAR(100) NOT NULL,
                cedula NVARCHAR(20) NOT NULL UNIQUE,
                telefono NVARCHAR(20),
                email NVARCHAR(100),
                direccion NVARCHAR(200),
                fecha_registro DATETIME2 DEFAULT GETDATE(),
                activo BIT DEFAULT 1
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createVentasTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ventas' AND xtype='U')
            CREATE TABLE ventas (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                numero_venta NVARCHAR(50) NOT NULL UNIQUE,
                cliente_id BIGINT,
                usuario_id BIGINT NOT NULL,
                fecha_venta DATETIME2 DEFAULT GETDATE(),
                subtotal DECIMAL(10,2) DEFAULT 0,
                impuesto DECIMAL(10,2) DEFAULT 0,
                descuento DECIMAL(10,2) DEFAULT 0,
                total DECIMAL(10,2) DEFAULT 0,
                estado NVARCHAR(20) DEFAULT 'PENDIENTE',
                observaciones NVARCHAR(300),
                metodo_pago NVARCHAR(50),
                FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void createDetalleVentasTable(Connection conn) throws SQLException {
        String sql = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='detalle_ventas' AND xtype='U')
            CREATE TABLE detalle_ventas (
                id BIGINT IDENTITY(1,1) PRIMARY KEY,
                venta_id BIGINT NOT NULL,
                producto_id BIGINT NOT NULL,
                cantidad INT NOT NULL,
                precio_unitario DECIMAL(10,2) NOT NULL,
                subtotal DECIMAL(10,2) NOT NULL,
                observaciones NVARCHAR(200),
                FOREIGN KEY (venta_id) REFERENCES ventas(id),
                FOREIGN KEY (producto_id) REFERENCES productos(id)
            )
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void insertInitialData(Connection conn) throws SQLException {
        // Insertar roles por defecto
        insertDefaultRoles(conn);

        // Insertar usuario administrador por defecto
        insertDefaultAdmin(conn);
    }

    private void insertDefaultRoles(Connection conn) throws SQLException {
        // Verificar si ya existen roles
        String checkSql = "SELECT COUNT(*) FROM roles";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Ya hay roles, no insertar
            }
        }

        // Insertar roles por defecto
        String sql = """
            INSERT INTO roles (nombre, descripcion, puede_vender, puede_gestionar_inventario, 
                             puede_gestionar_usuarios, puede_generar_reportes, 
                             puede_gestionar_clientes, puede_gestionar_proveedores) VALUES
            ('ADMINISTRADOR', 'Acceso completo al sistema', 1, 1, 1, 1, 1, 1),
            ('VENDEDOR', 'Puede realizar ventas y gestionar clientes', 1, 0, 0, 0, 1, 0),
            ('INVENTARIO', 'Gestión de productos y proveedores', 0, 1, 0, 0, 0, 1)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private void insertDefaultAdmin(Connection conn) throws SQLException {
        // Verificar si ya existe el usuario admin
        String checkSql = "SELECT COUNT(*) FROM usuarios WHERE username = 'admin'";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Ya existe admin
            }
        }

        // Hash SHA-256 de "admin123"
        String passwordHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

        String sql = """
            INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id) VALUES
            ('admin', ?, 'Administrador', 'Sistema', 'admin@ferreteria.com', 
             (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'))
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene información de la base de datos
     */
    public String getDatabaseInfo() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            StringBuilder info = new StringBuilder();
            info.append("=== Información de la Base de Datos ===\n");
            info.append("Producto: ").append(meta.getDatabaseProductName()).append("\n");
            info.append("Versión: ").append(meta.getDatabaseProductVersion()).append("\n");
            info.append("Driver: ").append(meta.getDriverName()).append("\n");
            info.append("Versión del driver: ").append(meta.getDriverVersion()).append("\n");
            info.append("URL: ").append(meta.getURL()).append("\n");

            return info.toString();
        } catch (SQLException e) {
            return "Error al obtener información de la base de datos: " + e.getMessage();
        }
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}
