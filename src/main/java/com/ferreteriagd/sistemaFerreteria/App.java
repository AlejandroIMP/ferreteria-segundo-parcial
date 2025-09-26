package com.ferreteriagd.sistemaFerreteria;

import com.ferreteriagd.sistemaFerreteria.view.LoginView;
import com.ferreteriagd.sistemaFerreteria.controller.RolController;

/**
 * Clase principal de la aplicación del Sistema de Ferretería GD
 * Punto de entrada que inicia con la pantalla de autenticación
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Ferretería GD...");
        System.out.println("═══════════════════════════════════════");

        try {
            // Inicializar roles del sistema si es necesario
            System.out.println("Verificando configuración inicial del sistema...");
            RolController rolController = new RolController();
            rolController.inicializarRolesSistema();

            System.out.println("Sistema listo para usar.");
            System.out.println();

            // Iniciar el sistema con la pantalla de login
            LoginView loginView = new LoginView();
            loginView.mostrarMenuBienvenida();

        } catch (Exception e) {
            System.err.println("❌ Error crítico al iniciar la aplicación:");
            System.err.println(e.getMessage());
            e.printStackTrace();

            System.out.println("\nPresione Enter para salir...");
            try {
                System.in.read();
            } catch (Exception ignored) {}
        }

        System.out.println("\n¡Gracias por usar el Sistema de Ferretería GD!");
    }
}
