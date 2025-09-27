package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.ProveedorController;
import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import java.util.List;
import java.util.Scanner;

public class ProveedorView {
    private final Scanner scanner;
    private final ProveedorController proveedorController;

    public ProveedorView() {
        this.scanner = new Scanner(System.in);
        this.proveedorController = new ProveedorController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║            GESTIÓN DE PROVEEDORES             ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Proveedor                           ║");
            System.out.println("║  2. Listar Proveedores                        ║");
            System.out.println("║  3. Buscar Proveedor                          ║");
            System.out.println("║  4. Actualizar Proveedor                      ║");
            System.out.println("║  5. Eliminar Proveedor                        ║");
            System.out.println("║  6. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearProveedor();
                        break;
                    case 2:
                        listarProveedores();
                        break;
                    case 3:
                        buscarProveedor();
                        break;
                    case 4:
                        actualizarProveedor();
                        break;
                    case 5:
                        eliminarProveedor();
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

    private void crearProveedor() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           CREAR PROVEEDOR             ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Nombre del proveedor: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Razón social: ");
            String razonSocial = scanner.nextLine().trim();

            System.out.print("RUC/NIT: ");
            String ruc = scanner.nextLine().trim();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Dirección: ");
            String direccion = scanner.nextLine().trim();

            if (proveedorController.crearProveedor(nombre, razonSocial, ruc, telefono, email, direccion)) {
                System.out.println("✓ Proveedor creado exitosamente!");
            } else {
                System.out.println("Error al crear el proveedor. Puede que el RUC o email ya existan.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarProveedores() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          LISTA DE PROVEEDORES         ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Proveedor> proveedores = proveedorController.obtenerTodosLosProveedores();

            if (proveedores.isEmpty()) {
                System.out.println("No hay proveedores registrados.");
            } else {
                System.out.printf("%-5s %-25s %-15s %-20s %-15s %-25s %-10s%n",
                    "ID", "NOMBRE", "RUC", "RAZÓN SOCIAL", "TELÉFONO", "EMAIL", "ACTIVO");
                System.out.println("─".repeat(120));

                for (Proveedor proveedor : proveedores) {
                    System.out.printf("%-5d %-25s %-15s %-20s %-15s %-25s %-10s%n",
                        proveedor.getId(),
                        proveedor.getNombre().length() > 25 ? proveedor.getNombre().substring(0, 22) + "..." : proveedor.getNombre(),
                        proveedor.getRuc() != null ? proveedor.getRuc() : "",
                        proveedor.getRazonSocial() != null ?
                            (proveedor.getRazonSocial().length() > 20 ? proveedor.getRazonSocial().substring(0, 17) + "..." : proveedor.getRazonSocial()) : "",
                        proveedor.getTelefono() != null ? proveedor.getTelefono() : "",
                        proveedor.getEmail() != null ?
                            (proveedor.getEmail().length() > 25 ? proveedor.getEmail().substring(0, 22) + "..." : proveedor.getEmail()) : "",
                        proveedor.isActivo() ? "Sí" : "No");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los proveedores: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarProveedor() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           BUSCAR PROVEEDOR            ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            Proveedor proveedor = null;

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID del proveedor: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    proveedor = proveedorController.buscarPorId(id);
                    break;
                case 2:
                    System.out.print("Ingrese el nombre del proveedor: ");
                    String nombre = scanner.nextLine().trim();
                    List<Proveedor> proveedores = proveedorController.buscarPorNombre(nombre);
                    if (proveedores != null && !proveedores.isEmpty()) {
                        System.out.println("\nResultados encontrados:");
                        System.out.printf("%-5s %-25s %-15s %-20s%n",
                            "ID", "NOMBRE", "RUC", "TELÉFONO");
                        System.out.println("─".repeat(70));
                        for (Proveedor p : proveedores) {
                            System.out.printf("%-5d %-25s %-15s %-20s%n",
                                p.getId(),
                                p.getNombre().length() > 25 ? p.getNombre().substring(0, 22) + "..." : p.getNombre(),
                                p.getRuc() != null ? p.getRuc() : "",
                                p.getTelefono() != null ? p.getTelefono() : "");
                        }
                    } else {
                        System.out.println("No se encontraron proveedores con ese nombre.");
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            if (proveedor != null) {
                mostrarDetalleProveedor(proveedor);
            } else if (opcion == 1) {
                System.out.println("Proveedor no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void actualizarProveedor() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         ACTUALIZAR PROVEEDOR          ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del proveedor a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Proveedor proveedor = proveedorController.buscarPorId(id);
            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleProveedor(proveedor);
            System.out.println("\nIngrese los nuevos datos (deje vacío para mantener el valor actual):");

            System.out.print("Nombre [" + proveedor.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = proveedor.getNombre();

            System.out.print("Razón social [" + (proveedor.getRazonSocial() != null ? proveedor.getRazonSocial() : "") + "]: ");
            String razonSocial = scanner.nextLine().trim();
            if (razonSocial.isEmpty()) razonSocial = proveedor.getRazonSocial();

            System.out.print("Teléfono [" + (proveedor.getTelefono() != null ? proveedor.getTelefono() : "") + "]: ");
            String telefono = scanner.nextLine().trim();
            if (telefono.isEmpty()) telefono = proveedor.getTelefono();

            System.out.print("Email [" + (proveedor.getEmail() != null ? proveedor.getEmail() : "") + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = proveedor.getEmail();

            System.out.print("Dirección [" + (proveedor.getDireccion() != null ? proveedor.getDireccion() : "") + "]: ");
            String direccion = scanner.nextLine().trim();
            if (direccion.isEmpty()) direccion = proveedor.getDireccion();

            System.out.print("Contacto [" + (proveedor.getContacto() != null ? proveedor.getContacto() : "") + "]: ");
            String contacto = scanner.nextLine().trim();
            if (contacto.isEmpty()) contacto = proveedor.getContacto();

            System.out.print("Teléfono del contacto [" + (proveedor.getTelefonoContacto() != null ? proveedor.getTelefonoContacto() : "") + "]: ");
            String telefonoContacto = scanner.nextLine().trim();
            if (telefonoContacto.isEmpty()) telefonoContacto = proveedor.getTelefonoContacto();

            if (proveedorController.actualizarProveedor(id, nombre, razonSocial, telefono, email, direccion, contacto, telefonoContacto)) {
                System.out.println("✓ Proveedor actualizado exitosamente!");
            } else {
                System.out.println("Error al actualizar el proveedor.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void eliminarProveedor() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          ELIMINAR PROVEEDOR           ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del proveedor a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Proveedor proveedor = proveedorController.buscarPorId(id);
            if (proveedor == null) {
                System.out.println("Proveedor no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleProveedor(proveedor);
            System.out.print("\n¿Está seguro de que desea eliminar este proveedor? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                if (proveedorController.eliminarProveedor(id)) {
                    System.out.println("✓ Proveedor eliminado exitosamente!");
                } else {
                    System.out.println("Error al eliminar el proveedor. Puede que tenga productos asociados.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleProveedor(Proveedor proveedor) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("ID: " + proveedor.getId());
        System.out.println("Nombre: " + proveedor.getNombre());
        System.out.println("Razón Social: " + (proveedor.getRazonSocial() != null ? proveedor.getRazonSocial() : ""));
        System.out.println("NIT: " + (proveedor.getRuc() != null ? proveedor.getRuc() : ""));
        System.out.println("Teléfono: " + (proveedor.getTelefono() != null ? proveedor.getTelefono() : ""));
        System.out.println("Email: " + (proveedor.getEmail() != null ? proveedor.getEmail() : ""));
        System.out.println("Dirección: " + (proveedor.getDireccion() != null ? proveedor.getDireccion() : ""));
        System.out.println("Contacto: " + (proveedor.getContacto() != null ? proveedor.getContacto() : ""));
        System.out.println("Teléfono Contacto: " + (proveedor.getTelefonoContacto() != null ? proveedor.getTelefonoContacto() : ""));
        System.out.println("Activo: " + (proveedor.isActivo() ? "Sí" : "No"));
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
