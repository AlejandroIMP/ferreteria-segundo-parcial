package com.ferreteriagd.sistemaFerreteria;

import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;
import com.ferreteriagd.sistemaFerreteria.dao.*;
import com.ferreteriagd.sistemaFerreteria.model.*;

import java.math.BigDecimal;

/**
 * Clase principal para probar la conexión a la base de datos
 */
public class TestDatabaseConnection {

    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE FERRETERÍA - PRUEBA DE CONEXIÓN ===\n");

        // Obtener instancia del DatabaseManager
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Mostrar configuración
        dbManager.getConfig().printConfiguration();
        System.out.println();

        // Probar conexión
        System.out.println("Probando conexión a la base de datos...");
        if (dbManager.testConnection()) {
            System.out.println("Conexión exitosa!");
            System.out.println(dbManager.getDatabaseInfo());
        } else {
            System.out.println("Error de conexión. Verificar configuración.");
            return;
        }

        // Inicializar base de datos
        System.out.println("Inicializando base de datos...");
        if (dbManager.initializeDatabase()) {
            System.out.println("Base de datos inicializada correctamente!");
        } else {
            System.out.println("Error al inicializar base de datos.");
            return;
        }

        // Probar operaciones básicas
        probarOperacionesBasicas();

        System.out.println("\n=== PRUEBA COMPLETADA ===");
    }

    private static void probarOperacionesBasicas() {
        System.out.println("\n--- Probando operaciones básicas ---");

        try {
            // Probar RolDAO
            RolDAO rolDAO = new RolDAO();
            System.out.println("Roles disponibles: " + rolDAO.obtenerTodos().size());

            // Probar UsuarioDAO
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario admin = usuarioDAO.buscarPorUsername("admin");
            if (admin != null) {
                System.out.println("Usuario admin encontrado: " + admin.getNombreCompleto());
            }

            // Probar creación de cliente
            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente cliente = new Cliente("Juan", "Pérez", "12345678",
                                        "555-0001", "juan@email.com", "Calle 123");
            if (clienteDAO.crear(cliente)) {
                System.out.println("Cliente creado exitosamente con ID: " + cliente.getId());
            }

            // Probar creación de proveedor
            ProveedorDAO proveedorDAO = new ProveedorDAO();
            Proveedor proveedor = new Proveedor("Ferretería Central", "Ferretería Central S.A.",
                                              "12345678901", "555-2001", "ventas@central.com", "Av. Principal");
            if (proveedorDAO.crear(proveedor)) {
                System.out.println("Proveedor creado exitosamente con ID: " + proveedor.getId());
            }

            // Probar creación de producto
            ProductDAO productDAO = new ProductDAO();
            Producto producto = new Producto("MART001", "Martillo", "Martillo de carpintero", "Herramientas",
                                            new BigDecimal("25.00"), new BigDecimal("35.00"), 5, "Unidad");
            producto.setProveedor(proveedor);
            if (productDAO.crear(producto)) {
                System.out.println("Producto creado exitosamente con ID: " + producto.getId());
            }

            System.out.println("Todas las operaciones básicas funcionan correctamente!");

        } catch (Exception e) {
            System.err.println("Error en las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
