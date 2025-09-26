package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.UsuarioController;
import com.ferreteriagd.sistemaFerreteria.controller.RolController;
import com.ferreteriagd.sistemaFerreteria.model.Rol;

import java.util.List;
import java.util.Scanner;

public class RegistroView {
    private Scanner scanner;
    private UsuarioController usuarioController;
    private RolController rolController;

    public RegistroView() {
        this.scanner = new Scanner(System.in);
        this.usuarioController = new UsuarioController();
        this.rolController = new RolController();
    }

    public boolean mostrarRegistro() {
        limpiarPantalla();
        
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              REGISTRO DE USUARIO              ║");
        System.out.println("║           SISTEMA FERRETERÍA GD               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Recopilar datos del usuario
            System.out.print("Nombre de usuario: ");
            String username = scanner.nextLine().trim();
            
            if (username.isEmpty()) {
                System.out.println("El nombre de usuario no puede estar vacío.");
                return false;
            }

            // Verificar si el usuario ya existe
            if (usuarioController.buscarPorUsername(username) != null) {
                System.out.println("El nombre de usuario ya existe. Elija otro.");
                return false;
            }

            System.out.print("Contraseña (mínimo 6 caracteres): ");
            String password = scanner.nextLine();
            
            if (password.length() < 6) {
                System.out.println("La contraseña debe tener al menos 6 caracteres.");
                return false;
            }

            System.out.print("Confirmar contraseña: ");
            String confirmarPassword = scanner.nextLine();
            
            if (!password.equals(confirmarPassword)) {
                System.out.println("Las contraseñas no coinciden.");
                return false;
            }

            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return false;
            }

            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();
            
            if (apellido.isEmpty()) {
                System.out.println("El apellido no puede estar vacío.");
                return false;
            }

            System.out.print("Email (opcional): ");
            String email = scanner.nextLine().trim();
            
            // Validar email si se proporciona
            if (!email.isEmpty() && !isValidEmail(email)) {
                System.out.println("El formato del email no es válido.");
                return false;
            }

            // Verificar si el email ya existe
            if (!email.isEmpty() && usuarioController.buscarPorEmail(email) != null) {
                System.out.println("El email ya está registrado.");
                return false;
            }

            System.out.print("Teléfono (opcional): ");
            String telefono = scanner.nextLine().trim();

            // Mostrar roles disponibles
            Long rolId = seleccionarRol();
            if (rolId == null) {
                System.out.println("Debe seleccionar un rol válido.");
                return false;
            }

            // Mostrar resumen antes de confirmar
            System.out.println("\n═══════════════════════════════════════════════");
            System.out.println("RESUMEN DEL REGISTRO:");
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("Usuario: " + username);
            System.out.println("Nombre: " + nombre + " " + apellido);
            System.out.println("Email: " + (email.isEmpty() ? "No proporcionado" : email));
            System.out.println("Teléfono: " + (telefono.isEmpty() ? "No proporcionado" : telefono));
            
            Rol rolSeleccionado = rolController.buscarPorId(rolId);
            System.out.println("Rol: " + (rolSeleccionado != null ? rolSeleccionado.getNombre() : "No encontrado"));
            System.out.println("═══════════════════════════════════════════════");
            
            System.out.print("\n¿Confirma el registro? (s/n): ");
            String confirmacion = scanner.nextLine().trim().toLowerCase();

            if (confirmacion.equals("s") || confirmacion.equals("si")) {
                if (usuarioController.registrarUsuario(username, password, confirmarPassword, 
                                                     nombre, apellido, email, telefono, rolId)) {
                    System.out.println("Usuario registrado exitosamente!");
                    return true;
                } else {
                    System.out.println("Error al registrar el usuario. Intente nuevamente.");
                    return false;
                }
            } else {
                System.out.println("Registro cancelado.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error durante el registro: " + e.getMessage());
            return false;
        }
    }

    private Long seleccionarRol() {
        System.out.println("\nROLES DISPONIBLES:");
        System.out.println("═══════════════════");

        List<Rol> roles = rolController.listarActivos();
        
        if (roles.isEmpty()) {
            System.out.println("No hay roles disponibles.");
            return null;
        }

        for (int i = 0; i < roles.size(); i++) {
            Rol rol = roles.get(i);
            System.out.println((i + 1) + ". " + rol.getNombre() + " - " + rol.getDescripcion());
        }

        System.out.print("\nSeleccione un rol (número): ");
        
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            
            if (opcion >= 1 && opcion <= roles.size()) {
                Rol rolSeleccionado = roles.get(opcion - 1);
                
                // Mostrar permisos del rol seleccionado
                System.out.println("\nPermisos del rol '" + rolSeleccionado.getNombre() + "':");
                System.out.println("─────────────────────────────────────────────");
                System.out.println("• Realizar ventas: " + (rolSeleccionado.isPuedeVender() ? "si" : "no"));
                System.out.println("• Gestionar inventario: " + (rolSeleccionado.isPuedeGestionarInventario() ? "si" : "no"));
                System.out.println("• Gestionar usuarios: " + (rolSeleccionado.isPuedeGestionarUsuarios() ? "si" : "no"));
                System.out.println("• Generar reportes: " + (rolSeleccionado.isPuedeGenerarReportes() ? "si" : "no"));
                System.out.println("• Gestionar clientes: " + (rolSeleccionado.isPuedeGestionarClientes() ? "si" : "no"));
                System.out.println("• Gestionar proveedores: " + (rolSeleccionado.isPuedeGestionarProveedores() ? "si" : "no"));
                
                System.out.print("\n¿Confirma este rol? (s/n): ");
                String confirmacion = scanner.nextLine().trim().toLowerCase();
                
                if (confirmacion.equals("s") || confirmacion.equals("si")) {
                    return rolSeleccionado.getId();
                } else {
                    return seleccionarRol(); // Volver a mostrar la lista
                }
            } else {
                System.out.println("Opción no válida.");
                return seleccionarRol();
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingrese un número válido.");
            return seleccionarRol();
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
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
}
