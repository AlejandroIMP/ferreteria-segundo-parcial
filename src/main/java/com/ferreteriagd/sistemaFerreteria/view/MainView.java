package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.SistemaController;
import com.ferreteriagd.sistemaFerreteria.controller.UsuarioController;
import com.ferreteriagd.sistemaFerreteria.model.Usuario;
import java.util.Scanner;

public class MainView {
    private Scanner scanner;
    private ClienteView clienteView;
    private ProductView productView;
    private ProveedorView proveedorView;
    private VentaView ventaView;
    private UsuarioView usuarioView;
    private RolView rolView;
    private HistorialVentasView historialVentasView;
    private SistemaController sistemaController;
    private UsuarioController usuarioController;

    // Constructor por defecto para compatibilidad
    public MainView() {
        this(new UsuarioController());
    }

    // Constructor que recibe el controlador de usuario con sesión iniciada
    public MainView(UsuarioController usuarioController) {
        this.scanner = new Scanner(System.in);
        this.clienteView = new ClienteView();
        this.productView = new ProductView();
        this.proveedorView = new ProveedorView();
        this.ventaView = new VentaView();
        this.usuarioView = new UsuarioView();
        this.rolView = new RolView();
        this.historialVentasView = new HistorialVentasView();
        this.sistemaController = new SistemaController();
        this.usuarioController = usuarioController;
    }

    public void mostrarMenu() {
        if (!usuarioController.isUsuarioLogueado()) {
            System.out.println("Debe iniciar sesión para acceder al sistema.");
            return;
        }

        boolean continuar = true;
        Usuario usuarioActual = usuarioController.getUsuarioActual();

        while (continuar) {
            limpiarPantalla();
            mostrarEncabezado(usuarioActual);
            mostrarOpcionesMenu(usuarioActual);

            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                continuar = procesarOpcion(opcion, usuarioActual);
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
                pausar();
            }
        }
    }

    private void mostrarEncabezado(Usuario usuario) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║           SISTEMA DE FERRETERÍA GD            ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  Usuario: " + String.format("%-32s", usuario.getNombreCompleto()) + " ║");
        System.out.println("║  Rol: " + String.format("%-36s", usuario.getRol().getNombre()) + " ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    private void mostrarOpcionesMenu(Usuario usuario) {
        int opcionNum = 1;

        // Gestión de Clientes (solo si tiene permisos)
        if (usuarioController.puedeGestionarClientes()) {
            System.out.println("  " + opcionNum++ + ". Gestión de Clientes");
        }

        // Gestión de Productos/Inventario (solo si tiene permisos)
        if (usuarioController.puedeGestionarInventario()) {
            System.out.println("  " + opcionNum++ + ". Gestión de Productos");
        }

        // Gestión de Proveedores (solo si tiene permisos)
        if (usuarioController.puedeGestionarProveedores()) {
            System.out.println("  " + opcionNum++ + ". Gestión de Proveedores");
        }

        // Gestión de Ventas (solo si puede vender)
        if (usuarioController.puedeVender()) {
            System.out.println("  " + opcionNum++ + ". Gestión de Ventas");
            System.out.println("  " + opcionNum++ + ". Historial de Ventas");
        }

        // Gestión de Usuarios (solo si tiene permisos)
        if (usuarioController.puedeGestionarUsuarios()) {
            System.out.println("  " + opcionNum++ + ". Gestión de Usuarios");
            System.out.println("  " + opcionNum++ + ". Gestión de Roles");
        }

        // Dashboard (solo si puede generar reportes)
        if (usuarioController.puedeGenerarReportes()) {
            System.out.println("  " + opcionNum++ + ". Dashboard del Sistema");
        }

        // Opciones disponibles para todos los usuarios
        System.out.println("  " + opcionNum++ + ". Cambiar Contraseña");
        System.out.println("  " + opcionNum++ + ". Cerrar Sesión");
        System.out.println("  " + opcionNum + ". Salir");
    }

    private boolean procesarOpcion(int opcion, Usuario usuario) {
        int opcionActual = 1;

        // Gestión de Clientes
        if (usuarioController.puedeGestionarClientes()) {
            if (opcion == opcionActual++) {
                clienteView.mostrarMenu();
                return true;
            }
        }

        // Gestión de Productos
        if (usuarioController.puedeGestionarInventario()) {
            if (opcion == opcionActual++) {
                productView.mostrarMenu();
                return true;
            }
        }

        // Gestión de Proveedores
        if (usuarioController.puedeGestionarProveedores()) {
            if (opcion == opcionActual++) {
                proveedorView.mostrarMenu();
                return true;
            }
        }

        // Gestión de Ventas
        if (usuarioController.puedeVender()) {
            if (opcion == opcionActual++) {
                ventaView.mostrarMenu();
                return true;
            }
            // Historial de Ventas
            if (opcion == opcionActual++) {
                historialVentasView.mostrarMenu();
                return true;
            }
        }

        // Gestión de Usuarios
        if (usuarioController.puedeGestionarUsuarios()) {
            if (opcion == opcionActual++) {
                usuarioView.mostrarMenu();
                return true;
            }
            // Gestión de Roles
            if (opcion == opcionActual++) {
                rolView.mostrarMenu();
                return true;
            }
        }

        // Dashboard
        if (usuarioController.puedeGenerarReportes()) {
            if (opcion == opcionActual++) {
                mostrarDashboard();
                return true;
            }
        }

        // Cambiar Contraseña
        if (opcion == opcionActual++) {
            cambiarContrasena();
            return true;
        }

        // Cerrar Sesión
        if (opcion == opcionActual++) {
            return cerrarSesion();
        }

        // Salir
        if (opcion == opcionActual) {
            return confirmarSalida();
        }

        System.out.println("Opción no válida.");
        pausar();
        return true;
    }

    private void mostrarDashboard() {
        limpiarPantalla();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              DASHBOARD DEL SISTEMA            ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();

        // Mostrar información del usuario
        System.out.println("INFORMACIÓN DE LA SESIÓN:");
        System.out.println("─────────────────────────");
        System.out.println(usuarioController.getInfoDashboard());

        // Mostrar dashboard del sistema
        try {
            var dashboardInfo = sistemaController.obtenerInfoDashboard();
            System.out.println("\nRESUMEN DEL SISTEMA:");
            System.out.println("─────────────────────");
            System.out.println(dashboardInfo);
        } catch (Exception e) {
            System.out.println("Error al cargar información del dashboard: " + e.getMessage());
        }

        pausar();
    }

    private void cambiarContrasena() {
        limpiarPantalla();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              CAMBIAR CONTRASEÑA               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();

        System.out.print("Contraseña actual: ");
        String passwordActual = scanner.nextLine();

        System.out.print("Nueva contraseña (mínimo 6 caracteres): ");
        String nuevaPassword = scanner.nextLine();

        if (nuevaPassword.length() < 6) {
            System.out.println("La contraseña debe tener al menos 6 caracteres.");
            pausar();
            return;
        }

        System.out.print("Confirmar nueva contraseña: ");
        String confirmarPassword = scanner.nextLine();

        if (!nuevaPassword.equals(confirmarPassword)) {
            System.out.println("Las contraseñas no coinciden.");
            pausar();
            return;
        }

        if (usuarioController.cambiarPassword(passwordActual, nuevaPassword)) {
            System.out.println("Contraseña cambiada exitosamente.");
        } else {
            System.out.println("Error al cambiar la contraseña. Verifique que la contraseña actual sea correcta.");
        }

        pausar();
    }

    private boolean cerrarSesion() {
        limpiarPantalla();
        System.out.println("¿Está seguro que desea cerrar sesión? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s") || respuesta.equals("si")) {
            usuarioController.cerrarSesion();
            System.out.println("Sesión cerrada exitosamente.");

            // Volver al menú de login
            LoginView loginView = new LoginView();
            loginView.mostrarMenuBienvenida();
            return false;
        }

        return true;
    }

    private boolean confirmarSalida() {
        limpiarPantalla();
        System.out.println("¿Está seguro que desea salir del sistema? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("s") || respuesta.equals("si")) {
            usuarioController.cerrarSesion();
            System.out.println("¡Gracias por usar el Sistema de Ferretería GD!");
            return false;
        }

        return true;
    }

    private void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Si falla la limpieza, simplemente imprimir líneas en blanco
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private void pausar() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
