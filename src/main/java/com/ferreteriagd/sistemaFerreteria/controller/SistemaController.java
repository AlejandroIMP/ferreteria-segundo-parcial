package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.*;
 import com.ferreteriagd.sistemaFerreteria.dao.*;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador principal del sistema de ferretería
 * Coordina todos los controladores y proporciona una interfaz unificada con base de datos
 */
public class SistemaController {
    private ProductController productController;
    private ClienteController clienteController;
    private ProveedorController proveedorController;
    private UsuarioController usuarioController;
    private VentaController ventaController;
    private RolController rolController;
    private DatabaseManager databaseManager;
    private boolean sistemaInicializado;

    public SistemaController() {
        this.databaseManager = DatabaseManager.getInstance();
        inicializarControladores();
        inicializarSistema();
    }

    // Inicializar todos los controladores
    private void inicializarControladores() {
        this.rolController = new RolController();
        this.productController = new ProductController();
        this.clienteController = new ClienteController();
        this.proveedorController = new ProveedorController();
        this.usuarioController = new UsuarioController();
        this.ventaController = new VentaController(usuarioController);
        this.sistemaInicializado = false;
    }

    // Inicializar sistema con datos por defecto
    public boolean inicializarSistema() {
        if (!sistemaInicializado) {
            // Verificar conexión a la base de datos
            if (!databaseManager.testConnection()) {
                System.err.println("Error: No se puede conectar a la base de datos");
                return false;
            }

            // Inicializar base de datos (crear tablas y datos iniciales)
            if (!databaseManager.initializeDatabase()) {
                System.err.println("Error: No se puede inicializar la base de datos");
                return false;
            }

            System.out.println("Sistema inicializado correctamente con base de datos");
            sistemaInicializado = true;
            return true;
        }
        return true;
    }

    // === MÉTODOS DE AUTENTICACIÓN ===
    public boolean login(String username, String password) {
        return usuarioController.autenticar(username, password);
    }

    public void logout() {
        usuarioController.cerrarSesion();
    }

    public Usuario getUsuarioActual() {
        return usuarioController.getUsuarioActual();
    }

    public boolean usuarioLogueado() {
        return usuarioController.getUsuarioActual() != null;
    }

    // === MÉTODOS DE VALIDACIÓN DE PERMISOS ===
    public boolean puedeVender() {
        return usuarioController.puedeVender();
    }

    public boolean puedeGestionarInventario() {
        return usuarioController.puedeGestionarInventario();
    }

    public boolean puedeGestionarUsuarios() {
        return usuarioController.puedeGestionarUsuarios();
    }

    public boolean puedeGenerarReportes() {
        return usuarioController.puedeGenerarReportes();
    }

    public boolean puedeGestionarClientes() {
        return usuarioController.puedeGestionarClientes();
    }

    public boolean puedeGestionarProveedores() {
        return usuarioController.puedeGestionarProveedores();
    }

    // === ACCESO A CONTROLADORES ESPECÍFICOS ===
    public ProductController getProductController() {
        return productController;
    }

    public ClienteController getClienteController() {
        return clienteController;
    }

    public ProveedorController getProveedorController() {
        return proveedorController;
    }

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public VentaController getVentaController() {
        return ventaController;
    }

    public RolController getRolController() {
        return rolController;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    // === MÉTODOS DE DASHBOARD Y ESTADÍSTICAS ===
    public DashboardInfo obtenerInfoDashboard() {
        DashboardInfo info = new DashboardInfo();

        try {
            // Estadísticas de productos
            info.totalProductos = productController.contarProductosActivos();
            info.productosSinStock = productController.contarProductosSinStock();
            info.valorInventario = productController.calcularValorTotalInventario();

            // Estadísticas de ventas del día
            info.ventasDelDia = ventaController.contarVentasDelDia();
            info.ingresosDia = ventaController.calcularVentasDelDia();

            // Estadísticas de clientes
            info.totalClientes = clienteController.contarClientesActivos();

            // Estadísticas de usuarios
            info.usuariosActivos = usuarioController.contarUsuariosActivos();

            // Productos con stock bajo
            info.productosStockBajo = productController.obtenerProductosConStockBajo();

        } catch (Exception e) {
            System.err.println("Error al obtener información del dashboard: " + e.getMessage());
        }

        return info;
    }

    // === MÉTODOS DE BÚSQUEDA GLOBAL ===
    public ResultadoBusqueda buscarGlobal(String termino) {
        ResultadoBusqueda resultado = new ResultadoBusqueda();

        try {
            // Buscar en productos
            resultado.productos = productController.buscarPorNombre(termino);

            // Buscar en clientes
            resultado.clientes = clienteController.buscarPorNombre(termino);

            // Buscar en proveedores
            resultado.proveedores = proveedorController.buscarPorNombre(termino);

            // Buscar ventas por número
            Venta venta = ventaController.buscarPorNumero(termino);
            if (venta != null) {
                resultado.ventas = List.of(venta);
            }

        } catch (Exception e) {
            System.err.println("Error en búsqueda global: " + e.getMessage());
        }

        return resultado;
    }

    // === MÉTODOS DE BACKUP Y RESTAURACIÓN ===
    public boolean realizarBackup() {
        try {
            // Implementar lógica de backup usando los DAOs
            System.out.println("Funcionalidad de backup en desarrollo");
            return true;
        } catch (Exception e) {
            System.err.println("Error al realizar backup: " + e.getMessage());
            return false;
        }
    }

    public boolean restaurarBackup(String archivoBackup) {
        try {
            // Implementar lógica de restauración
            System.out.println("Funcionalidad de restauración en desarrollo");
            return true;
        } catch (Exception e) {
            System.err.println("Error al restaurar backup: " + e.getMessage());
            return false;
        }
    }

    // === MÉTODOS DE VALIDACIÓN DE INTEGRIDAD ===
    public boolean validarIntegridadDatos() {
        try {
            // Verificar conexión a la base de datos
            if (!databaseManager.testConnection()) {
                return false;
            }

            // Validar que existan roles básicos
            if (rolController.obtenerTodosLosRoles().isEmpty()) {
                return false;
            }

            // Validar que existe al menos un usuario administrador
            if (usuarioController.obtenerUsuariosPorRol("ADMINISTRADOR").isEmpty()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error al validar integridad: " + e.getMessage());
            return false;
        }
    }

    // === CONFIGURACIÓN DEL SISTEMA ===
    public void configurarImpuestos(BigDecimal porcentajeImpuesto) {
        // Implementar configuración de impuestos
        System.out.println("Configuración de impuestos: " + porcentajeImpuesto + "%");
    }

    public void configurarMoneda(String simboloMoneda, String nombreMoneda) {
        // Implementar configuración de moneda
        System.out.println("Moneda configurada: " + nombreMoneda + " (" + simboloMoneda + ")");
    }

    // === MÉTODOS DE MANTENIMIENTO ===
    public void limpiarDatosTemporales() {
        // Limpiar datos temporales
        System.out.println("Limpieza de datos temporales en desarrollo");
    }

    public void optimizarRendimiento() {
        // Implementar optimizaciones de rendimiento
        System.out.println("Optimización de rendimiento en desarrollo");
    }

    // === CLASES AUXILIARES PARA INFORMACIÓN ===
    public static class DashboardInfo {
        public int totalProductos;
        public int productosSinStock;
        public BigDecimal valorInventario = BigDecimal.ZERO;
        public int ventasDelDia;
        public BigDecimal ingresosDia = BigDecimal.ZERO;
        public int totalClientes;
        public int usuariosActivos;
        public List<Producto> productosStockBajo;
    }

    public static class ResultadoBusqueda {
        public List<Producto> productos = List.of();
        public List<Cliente> clientes = List.of();
        public List<Proveedor> proveedores = List.of();
        public List<Venta> ventas = List.of();
    }

    // === MÉTODOS DE INICIALIZACIÓN DE DATOS DE PRUEBA ===
    public void cargarDatosPrueba() {
        try {
            if (puedeGestionarInventario()) {
                cargarProductosPrueba();
            }
            if (puedeGestionarClientes()) {
                cargarClientesPrueba();
            }
            if (puedeGestionarProveedores()) {
                cargarProveedoresPrueba();
            }
            System.out.println("Datos de prueba cargados correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
        }
    }

    private void cargarProductosPrueba() {
        // Crear algunos proveedores de prueba
        proveedorController.crearProveedor("Ferretería Central", "Ferretería Central S.A.",
                                          "12345678901", "555-0001", "ventas@ferreteriacentral.com",
                                          "Av. Principal 123");

        // Buscar el proveedor creado
        Proveedor proveedor = proveedorController.buscarPorRuc("12345678901");
        if (proveedor != null) {
            // Crear algunos productos de prueba
            productController.crearProducto("MART001", "Martillo de Carpintero",
                                           "Martillo profesional para carpintería", "Herramientas",
                                           "Stanley", "Classic", new BigDecimal("25.00"),
                                           new BigDecimal("35.00"), 5, "Unidad", proveedor.getId());

            productController.crearProducto("TORN002", "Tornillos 2\"",
                                           "Tornillos galvanizados de 2 pulgadas", "Ferretería",
                                           "Genérico", "", new BigDecimal("0.10"),
                                           new BigDecimal("0.15"), 100, "Unidad", proveedor.getId());
        }
    }

    private void cargarClientesPrueba() {
        clienteController.crearCliente("Juan", "Pérez", "12345678",
                                      "555-1001", "juan.perez@email.com",
                                      "Calle 123, Ciudad");

        clienteController.crearCliente("María", "González", "87654321",
                                      "555-1002", "maria.gonzalez@email.com",
                                      "Avenida 456, Ciudad");
    }

    private void cargarProveedoresPrueba() {
        proveedorController.crearProveedor("Distribuidora Norte", "Distribuidora Norte LTDA",
                                          "98765432109", "555-2001", "info@distnorte.com",
                                          "Zona Industrial Norte");
    }

    // === INFORMACIÓN DEL SISTEMA ===
    public String obtenerInformacionSistema() {
        StringBuilder info = new StringBuilder();
        info.append("=== SISTEMA DE FERRETERÍA ===\n");
        info.append("Estado: ").append(sistemaInicializado ? "Inicializado" : "No inicializado").append("\n");
        info.append("Usuario actual: ").append(getUsuarioActual() != null ? getUsuarioActual().getUsername() : "Ninguno").append("\n");
        info.append("Base de datos: ").append(databaseManager.testConnection() ? "Conectada" : "Desconectada").append("\n");
        info.append("\n").append(databaseManager.getDatabaseInfo());
        return info.toString();
    }
}
