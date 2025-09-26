package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.RolController;
import com.ferreteriagd.sistemaFerreteria.model.Rol;
import java.util.List;
import java.util.Scanner;

public class RolView {
    private Scanner scanner;
    private RolController rolController;

    public RolView() {
        this.scanner = new Scanner(System.in);
        this.rolController = new RolController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║              GESTIÓN DE ROLES                 ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Rol                                 ║");
            System.out.println("║  2. Listar Roles                              ║");
            System.out.println("║  3. Buscar Rol                                ║");
            System.out.println("║  4. Actualizar Rol                            ║");
            System.out.println("║  5. Eliminar Rol                              ║");
            System.out.println("║  6. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearRol();
                        break;
                    case 2:
                        listarRoles();
                        break;
                    case 3:
                        buscarRol();
                        break;
                    case 4:
                        actualizarRol();
                        break;
                    case 5:
                        eliminarRol();
                        break;
                    case 6:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción no válida. Presione Enter para continuar...");
                        scanner.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido. Presione Enter para continuar...");
                scanner.nextLine();
            }
        }
    }

    private void crearRol() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("             CREAR ROL                 ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Nombre del rol: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            System.out.println("\nPermisos del rol:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            System.out.print("¿Puede realizar ventas? (S/N): ");
            boolean puedeVender = scanner.nextLine().trim().toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar inventario? (S/N): ");
            boolean puedeGestionarInventario = scanner.nextLine().trim().toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar usuarios? (S/N): ");
            boolean puedeGestionarUsuarios = scanner.nextLine().trim().toUpperCase().startsWith("S");

            System.out.print("¿Puede generar reportes? (S/N): ");
            boolean puedeGenerarReportes = scanner.nextLine().trim().toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar clientes? (S/N): ");
            boolean puedeGestionarClientes = scanner.nextLine().trim().toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar proveedores? (S/N): ");
            boolean puedeGestionarProveedores = scanner.nextLine().trim().toUpperCase().startsWith("S");

            if (rolController.crearRol(nombre, descripcion, puedeVender, puedeGestionarInventario,
                                     puedeGestionarUsuarios, puedeGenerarReportes,
                                     puedeGestionarClientes, puedeGestionarProveedores)) {
                System.out.println("✓ Rol creado exitosamente!");
            } else {
                System.out.println("✗ Error al crear el rol. Verifique que el nombre no exista.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarRoles() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            LISTA DE ROLES             ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Rol> roles = rolController.obtenerTodosLosRoles();

            if (roles.isEmpty()) {
                System.out.println("No hay roles registrados.");
            } else {
                System.out.printf("%-5s %-20s %-40s %-10s%n",
                    "ID", "NOMBRE", "DESCRIPCIÓN", "ACTIVO");
                System.out.println("─".repeat(80));

                for (Rol rol : roles) {
                    System.out.printf("%-5d %-20s %-40s %-10s%n",
                        rol.getId(),
                        rol.getNombre().length() > 20 ? rol.getNombre().substring(0, 17) + "..." : rol.getNombre(),
                        rol.getDescripcion().length() > 40 ? rol.getDescripcion().substring(0, 37) + "..." : rol.getDescripcion(),
                        rol.isActivo() ? "Sí" : "No");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Error al cargar los roles: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarRol() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("             BUSCAR ROL                ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            Rol rol = null;

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID del rol: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    rol = rolController.buscarPorId(id);
                    break;
                case 2:
                    System.out.print("Ingrese el nombre del rol: ");
                    String nombre = scanner.nextLine().trim();
                    rol = rolController.buscarPorNombre(nombre);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            if (rol != null) {
                mostrarDetalleRol(rol);
            } else {
                System.out.println("Rol no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void actualizarRol() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           ACTUALIZAR ROL              ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del rol a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Rol rol = rolController.buscarPorId(id);
            if (rol == null) {
                System.out.println("Rol no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleRol(rol);
            System.out.println("\nIngrese los nuevos datos (deje vacío para mantener el valor actual):");

            System.out.print("Descripción [" + rol.getDescripcion() + "]: ");
            String descripcion = scanner.nextLine().trim();
            if (descripcion.isEmpty()) descripcion = rol.getDescripcion();

            System.out.println("\nPermisos del rol (deje vacío para mantener el valor actual):");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            System.out.print("¿Puede realizar ventas? (S/N) [" + (rol.isPuedeVender() ? "S" : "N") + "]: ");
            String ventaStr = scanner.nextLine().trim();
            boolean puedeVender = ventaStr.isEmpty() ? rol.isPuedeVender() : ventaStr.toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar inventario? (S/N) [" + (rol.isPuedeGestionarInventario() ? "S" : "N") + "]: ");
            String inventarioStr = scanner.nextLine().trim();
            boolean puedeGestionarInventario = inventarioStr.isEmpty() ? rol.isPuedeGestionarInventario() : inventarioStr.toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar usuarios? (S/N) [" + (rol.isPuedeGestionarUsuarios() ? "S" : "N") + "]: ");
            String usuariosStr = scanner.nextLine().trim();
            boolean puedeGestionarUsuarios = usuariosStr.isEmpty() ? rol.isPuedeGestionarUsuarios() : usuariosStr.toUpperCase().startsWith("S");

            System.out.print("¿Puede generar reportes? (S/N) [" + (rol.isPuedeGenerarReportes() ? "S" : "N") + "]: ");
            String reportesStr = scanner.nextLine().trim();
            boolean puedeGenerarReportes = reportesStr.isEmpty() ? rol.isPuedeGenerarReportes() : reportesStr.toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar clientes? (S/N) [" + (rol.isPuedeGestionarClientes() ? "S" : "N") + "]: ");
            String clientesStr = scanner.nextLine().trim();
            boolean puedeGestionarClientes = clientesStr.isEmpty() ? rol.isPuedeGestionarClientes() : clientesStr.toUpperCase().startsWith("S");

            System.out.print("¿Puede gestionar proveedores? (S/N) [" + (rol.isPuedeGestionarProveedores() ? "S" : "N") + "]: ");
            String proveedoresStr = scanner.nextLine().trim();
            boolean puedeGestionarProveedores = proveedoresStr.isEmpty() ? rol.isPuedeGestionarProveedores() : proveedoresStr.toUpperCase().startsWith("S");

            if (rolController.actualizarRol(id, descripcion, puedeVender, puedeGestionarInventario,
                                          puedeGestionarUsuarios, puedeGenerarReportes,
                                          puedeGestionarClientes, puedeGestionarProveedores)) {
                System.out.println("✓ Rol actualizado exitosamente!");
            } else {
                System.out.println("✗ Error al actualizar el rol.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void eliminarRol() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            ELIMINAR ROL               ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del rol a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Rol rol = rolController.buscarPorId(id);
            if (rol == null) {
                System.out.println("Rol no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleRol(rol);
            System.out.print("\n¿Está seguro de que desea eliminar este rol? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                if (rolController.eliminarRol(id)) {
                    System.out.println("✓ Rol eliminado exitosamente!");
                } else {
                    System.out.println("✗ Error al eliminar el rol. Puede que haya usuarios asociados a este rol o sea un rol por defecto.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleRol(Rol rol) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("ID: " + rol.getId());
        System.out.println("Nombre: " + rol.getNombre());
        System.out.println("Descripción: " + rol.getDescripcion());
        System.out.println("Activo: " + (rol.isActivo() ? "Sí" : "No"));
        System.out.println("═".repeat(50));
    }

    private void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
