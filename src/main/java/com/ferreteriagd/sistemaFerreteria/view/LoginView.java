package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.UsuarioController;
import com.ferreteriagd.sistemaFerreteria.model.Usuario;

import java.util.Scanner;

public class LoginView {
    private Scanner scanner;
    private UsuarioController usuarioController;

    public LoginView() {
        this.scanner = new Scanner(System.in);
        this.usuarioController = new UsuarioController();
    }

    public Usuario mostrarLogin() {
        limpiarPantalla();

        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║              INICIO DE SESIÓN                 ║");
        System.out.println("║           SISTEMA FERRETERÍA GD               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();

        int intentos = 0;
        int maxIntentos = 3;

        while (intentos < maxIntentos) {
            System.out.print("Usuario: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("❌ El usuario no puede estar vacío.");
                continue;
            }

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();

            if (password.isEmpty()) {
                System.out.println("❌ La contraseña no puede estar vacía.");
                continue;
            }

            if (usuarioController.iniciarSesion(username, password)) {
                Usuario usuario = usuarioController.getUsuarioActual();
                System.out.println("✅ Bienvenido, " + usuario.getNombreCompleto() + "!");
                System.out.println("Rol: " + usuario.getRol().getNombre());

                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();

                return usuario;
            } else {
                intentos++;
                int intentosRestantes = maxIntentos - intentos;

                if (intentosRestantes > 0) {
                    System.out.println("❌ Usuario o contraseña incorrectos.");
                    System.out.println("Intentos restantes: " + intentosRestantes);
                    System.out.println();
                } else {
                    System.out.println("❌ Se han agotado los intentos de inicio de sesión.");
                    System.out.println("El sistema se cerrará por seguridad.");

                    System.out.println("\nPresione Enter para salir...");
                    scanner.nextLine();

                    return null;
                }
            }
        }

        return null;
    }

    public boolean mostrarMenuPrincipal() {
        limpiarPantalla();

        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║           SISTEMA FERRETERÍA GD               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. Iniciar Sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.println();
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    Usuario usuario = mostrarLogin();
                    if (usuario != null) {
                        // Transferir el usuario autenticado al sistema principal
                        MainView mainView = new MainView(usuarioController);
                        mainView.mostrarMenu();
                        return true;
                    }
                    break;

                case 2:
                    RegistroView registroView = new RegistroView();
                    if (registroView.mostrarRegistro()) {
                        System.out.println("✅ Usuario registrado exitosamente.");
                        System.out.println("Ahora puede iniciar sesión.");
                        System.out.println("\nPresione Enter para continuar...");
                        scanner.nextLine();
                    }
                    break;

                case 3:
                    System.out.println("¡Hasta luego!");
                    return false;

                default:
                    System.out.println("❌ Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor ingrese un número válido.");
        }

        if (scanner.hasNextLine()) {
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
        }

        return true;
    }

    public void mostrarMenuBienvenida() {
        boolean continuar = true;

        while (continuar) {
            continuar = mostrarMenuPrincipal();
        }
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

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }
}
