package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.UsuarioController;
import com.ferreteriagd.sistemaFerreteria.controller.RolController;
import com.ferreteriagd.sistemaFerreteria.model.Usuario;
import com.ferreteriagd.sistemaFerreteria.model.Rol;
import java.util.List;
import java.util.Scanner;

public class UsuarioView {
    private Scanner scanner;
    private UsuarioController usuarioController;
    private RolController rolController;

    public UsuarioView() {
        this.scanner = new Scanner(System.in);
        this.usuarioController = new UsuarioController();
        this.rolController = new RolController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║             GESTIÓN DE USUARIOS               ���");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Usuario                             ║");
            System.out.println("║  2. Listar Usuarios                           ║");
            System.out.println("║  3. Buscar Usuario                            ║");
            System.out.println("║  4. Actualizar Usuario                        ║");
            System.out.println("║  5. Eliminar Usuario                          ║");
            System.out.println("║  6. Cambiar Contraseña                        ║");
            System.out.println("║  7. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearUsuario();
                        break;
                    case 2:
                        listarUsuarios();
                        break;
                    case 3:
                        buscarUsuario();
                        break;
                    case 4:
                        actualizarUsuario();
                        break;
                    case 5:
                        eliminarUsuario();
                        break;
                    case 6:
                        cambiarContrasena();
                        break;
                    case 7:
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

    private void crearUsuario() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           CREAR USUARIO               ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Nombre de usuario: ");
            String nombreUsuario = scanner.nextLine().trim();

            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine().trim();

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Teléfono: ");
            String telefono = scanner.nextLine().trim();

            // Mostrar roles disponibles
            List<Rol> roles = rolController.obtenerTodosLosRoles();
            if (roles.isEmpty()) {
                System.out.println("No hay roles registrados. Debe crear un rol primero.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            System.out.println("\nRoles disponibles:");
            for (Rol rol : roles) {
                System.out.println(rol.getId() + " - " + rol.getNombre() + " (" + rol.getDescripcion() + ")");
            }

            System.out.print("Seleccione el ID del rol: ");
            Long idRol = Long.parseLong(scanner.nextLine().trim());

            if (usuarioController.crearUsuario(nombreUsuario, contrasena, nombre, apellido, email, telefono, idRol)) {
                System.out.println("✓ Usuario creado exitosamente!");
            } else {
                System.out.println("Error al crear el usuario. Verifique que el nombre de usuario no exista.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor, ingrese un número válido para el rol.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarUsuarios() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           LISTA DE USUARIOS           ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Usuario> usuarios = usuarioController.obtenerTodosLosUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
            } else {
                System.out.printf("%-5s %-20s %-25s %-25s %-15s %-10s%n",
                    "ID", "USUARIO", "NOMBRE", "EMAIL", "ROL", "ACTIVO");
                System.out.println("─".repeat(95));

                for (Usuario usuario : usuarios) {
                    String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "N/A";
                    System.out.printf("%-5d %-20s %-25s %-25s %-15s %-10s%n",
                        usuario.getId(),
                        usuario.getUsername().length() > 20 ? usuario.getUsername().substring(0, 17) + "..." : usuario.getUsername(),
                        usuario.getNombreCompleto().length() > 25 ? usuario.getNombreCompleto().substring(0, 22) + "..." : usuario.getNombreCompleto(),
                        (usuario.getEmail() != null && usuario.getEmail().length() > 25) ? usuario.getEmail().substring(0, 22) + "..." : (usuario.getEmail() != null ? usuario.getEmail() : "N/A"),
                        rolNombre.length() > 15 ? rolNombre.substring(0, 12) + "..." : rolNombre,
                        usuario.isActivo() ? "Sí" : "No");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los usuarios: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarUsuario() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            BUSCAR USUARIO             ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por nombre de usuario");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            Usuario usuario = null;

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID del usuario: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    usuario = usuarioController.buscarPorId(id);
                    break;
                case 2:
                    System.out.print("Ingrese el nombre de usuario: ");
                    String nombreUsuario = scanner.nextLine().trim();
                    usuario = usuarioController.buscarPorUsername(nombreUsuario);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            if (usuario != null) {
                mostrarDetalleUsuario(usuario);
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void actualizarUsuario() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         ACTUALIZAR USUARIO            ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del usuario a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Usuario usuario = usuarioController.buscarPorId(id);
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleUsuario(usuario);
            System.out.println("\nIngrese los nuevos datos (deje vacío para mantener el valor actual):");

            String nombre = usuario.getNombre();
            System.out.print("Nombre [" + nombre + "]: ");
            String nuevoNombre = scanner.nextLine().trim();
            if (!nuevoNombre.isEmpty()) nombre = nuevoNombre;

            String apellido = usuario.getApellido();
            System.out.print("Apellido [" + apellido + "]: ");
            String nuevoApellido = scanner.nextLine().trim();
            if (!nuevoApellido.isEmpty()) apellido = nuevoApellido;

            String email = usuario.getEmail();
            System.out.print("Email [" + (email != null ? email : "") + "]: ");
            String nuevoEmail = scanner.nextLine().trim();
            if (!nuevoEmail.isEmpty()) email = nuevoEmail;

            String telefono = usuario.getTelefono();
            System.out.print("Teléfono [" + (telefono != null ? telefono : "") + "]: ");
            String nuevoTelefono = scanner.nextLine().trim();
            if (!nuevoTelefono.isEmpty()) telefono = nuevoTelefono;

            // Mostrar roles disponibles
            List<Rol> roles = rolController.obtenerTodosLosRoles();
            Long rolId = usuario.getRol().getId();
            if (!roles.isEmpty()) {
                System.out.println("\nRoles disponibles:");
                for (Rol rol : roles) {
                    System.out.println(rol.getId() + " - " + rol.getNombre());
                }

                System.out.print("ID del rol [" + rolId + "]: ");
                String rolStr = scanner.nextLine().trim();
                if (!rolStr.isEmpty()) {
                    rolId = Long.parseLong(rolStr);
                }
            }

            if (usuarioController.actualizarUsuario(id, nombre, apellido, email, telefono, rolId)) {
                System.out.println("✓ Usuario actualizado exitosamente!");
            } else {
                System.out.println("Error al actualizar el usuario.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void eliminarUsuario() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          ELIMINAR USUARIO             ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del usuario a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Usuario usuario = usuarioController.buscarPorId(id);
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleUsuario(usuario);
            System.out.print("\n¿Está seguro de que desea eliminar este usuario? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                if (usuarioController.eliminarUsuario(id)) {
                    System.out.println("✓ Usuario eliminado exitosamente!");
                } else {
                    System.out.println("Error al eliminar el usuario. Puede que no tenga permisos o sea su propio usuario.");
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

    private void cambiarContrasena() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         CAMBIAR CONTRASEÑA            ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del usuario: ");
            Long id = Long.parseLong(scanner.nextLine());

            Usuario usuario = usuarioController.buscarPorId(id);
            if (usuario == null) {
                System.out.println("Usuario no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            System.out.println("Usuario: " + usuario.getUsername());
            System.out.print("Nueva contraseña: ");
            String nuevaContrasena = scanner.nextLine().trim();

            if (nuevaContrasena.isEmpty()) {
                System.out.println("La contraseña no puede estar vacía.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            if (usuarioController.restablecerPassword(id, nuevaContrasena)) {
                System.out.println("✓ Contraseña cambiada exitosamente!");
            } else {
                System.out.println("Error al cambiar la contraseña. Puede que no tenga permisos.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleUsuario(Usuario usuario) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("ID: " + usuario.getId());
        System.out.println("Usuario: " + usuario.getUsername());
        System.out.println("Nombre: " + usuario.getNombreCompleto());
        System.out.println("Email: " + (usuario.getEmail() != null ? usuario.getEmail() : "N/A"));
        System.out.println("Teléfono: " + (usuario.getTelefono() != null ? usuario.getTelefono() : "N/A"));
        System.out.println("Rol: " + (usuario.getRol() != null ? usuario.getRol().getNombre() : "N/A"));
        System.out.println("Activo: " + (usuario.isActivo() ? "Sí" : "No"));
        if (usuario.getFechaCreacion() != null) {
            System.out.println("Fecha Creación: " + usuario.getFechaCreacion());
        }
        if (usuario.getUltimoAcceso() != null) {
            System.out.println("Último Acceso: " + usuario.getUltimoAcceso());
        }
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
