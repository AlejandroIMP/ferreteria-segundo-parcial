package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.ClienteController;
import com.ferreteriagd.sistemaFerreteria.model.Cliente;
import java.util.List;
import java.util.Scanner;

public class ClienteView {
    private Scanner scanner;
    private ClienteController clienteController;

    public ClienteView() {
        this.scanner = new Scanner(System.in);
        this.clienteController = new ClienteController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║             GESTIÓN DE CLIENTES               ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Cliente                             ║");
            System.out.println("║  2. Listar Clientes                           ║");
            System.out.println("║  3. Buscar Cliente                            ║");
            System.out.println("║  4. Actualizar Cliente                        ║");
            System.out.println("║  5. Eliminar Cliente                          ║");
            System.out.println("║  6. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearCliente();
                        break;
                    case 2:
                        listarClientes();
                        break;
                    case 3:
                        buscarCliente();
                        break;
                    case 4:
                        actualizarCliente();
                        break;
                    case 5:
                        eliminarCliente();
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

    private void crearCliente() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           CREAR CLIENTE               ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();

            System.out.print("Cédula: ");
            String cedula = scanner.nextLine().trim();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Dirección: ");
            String direccion = scanner.nextLine().trim();

            if (clienteController.crearCliente(nombre, apellido, cedula, telefono, email, direccion)) {
                System.out.println("✓ Cliente creado exitosamente!");
            } else {
                System.out.println("Error al crear el cliente. Puede que la cédula o email ya existan.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarClientes() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           LISTA DE CLIENTES           ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Cliente> clientes = clienteController.obtenerTodosLosClientes();

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
            } else {
                System.out.printf("%-5s %-20s %-20s %-15s %-15s %-25s %-10s%n",
                    "ID", "NOMBRE", "APELLIDO", "CÉDULA", "TELÉFONO", "EMAIL", "ACTIVO");
                System.out.println("─".repeat(110));

                for (Cliente cliente : clientes) {
                    System.out.printf("%-5d %-20s %-20s %-15s %-15s %-25s %-10s%n",
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getApellido(),
                        cliente.getCedula(),
                        cliente.getTelefono() != null ? cliente.getTelefono() : "",
                        cliente.getEmail() != null ? cliente.getEmail() : "",
                        cliente.isActivo() ? "Sí" : "No");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los clientes: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarCliente() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           BUSCAR CLIENTE              ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por cédula");
        System.out.println("3. Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            Cliente cliente = null;
            List<Cliente> clientes = null;

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID del cliente: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    cliente = clienteController.buscarPorId(id);
                    if (cliente != null) {
                        mostrarDetalleCliente(cliente);
                    } else {
                        System.out.println("Cliente no encontrado.");
                    }
                    break;
                case 2:
                    System.out.print("Ingrese la cédula del cliente: ");
                    String cedula = scanner.nextLine().trim();
                    cliente = clienteController.buscarPorCedula(cedula);
                    if (cliente != null) {
                        mostrarDetalleCliente(cliente);
                    } else {
                        System.out.println("Cliente no encontrado.");
                    }
                    break;
                case 3:
                    System.out.print("Ingrese el nombre del cliente: ");
                    String nombre = scanner.nextLine().trim();
                    clientes = clienteController.buscarPorNombre(nombre);
                    if (clientes != null && !clientes.isEmpty()) {
                        System.out.println("\nResultados encontrados:");
                        System.out.printf("%-5s %-20s %-20s %-15s %-15s%n",
                            "ID", "NOMBRE", "APELLIDO", "CÉDULA", "TELÉFONO");
                        System.out.println("─".repeat(80));
                        for (Cliente c : clientes) {
                            System.out.printf("%-5d %-20s %-20s %-15s %-15s%n",
                                c.getId(),
                                c.getNombre(),
                                c.getApellido(),
                                c.getCedula(),
                                c.getTelefono() != null ? c.getTelefono() : "");
                        }
                    } else {
                        System.out.println("No se encontraron clientes con ese nombre.");
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void actualizarCliente() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         ACTUALIZAR CLIENTE            ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del cliente a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Cliente cliente = clienteController.buscarPorId(id);
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleCliente(cliente);
            System.out.println("\nIngrese los nuevos datos (deje vacío para mantener el valor actual):");

            System.out.print("Nombre [" + cliente.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = cliente.getNombre();

            System.out.print("Apellido [" + cliente.getApellido() + "]: ");
            String apellido = scanner.nextLine().trim();
            if (apellido.isEmpty()) apellido = cliente.getApellido();

            System.out.print("Teléfono [" + (cliente.getTelefono() != null ? cliente.getTelefono() : "") + "]: ");
            String telefono = scanner.nextLine().trim();
            if (telefono.isEmpty()) telefono = cliente.getTelefono();

            System.out.print("Email [" + (cliente.getEmail() != null ? cliente.getEmail() : "") + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = cliente.getEmail();

            System.out.print("Dirección [" + (cliente.getDireccion() != null ? cliente.getDireccion() : "") + "]: ");
            String direccion = scanner.nextLine().trim();
            if (direccion.isEmpty()) direccion = cliente.getDireccion();

            if (clienteController.actualizarCliente(id, nombre, apellido, telefono, email, direccion)) {
                System.out.println("✓ Cliente actualizado exitosamente!");
            } else {
                System.out.println("Error al actualizar el cliente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void eliminarCliente() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          ELIMINAR CLIENTE             ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del cliente a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Cliente cliente = clienteController.buscarPorId(id);
            if (cliente == null) {
                System.out.println("Cliente no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleCliente(cliente);
            System.out.print("\n¿Está seguro de que desea eliminar este cliente? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                if (clienteController.eliminarCliente(id)) {
                    System.out.println("✓ Cliente eliminado exitosamente!");
                } else {
                    System.out.println("Error al eliminar el cliente.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("ror: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleCliente(Cliente cliente) {
        System.out.println("\n" + "═".repeat(40));
        System.out.println("ID: " + cliente.getId());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Apellido: " + cliente.getApellido());
        System.out.println("Cédula: " + cliente.getCedula());
        System.out.println("Teléfono: " + (cliente.getTelefono() != null ? cliente.getTelefono() : ""));
        System.out.println("Email: " + (cliente.getEmail() != null ? cliente.getEmail() : ""));
        System.out.println("Dirección: " + (cliente.getDireccion() != null ? cliente.getDireccion() : ""));
        System.out.println("Activo: " + (cliente.isActivo() ? "Sí" : "No"));
        System.out.println("═".repeat(40));
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
