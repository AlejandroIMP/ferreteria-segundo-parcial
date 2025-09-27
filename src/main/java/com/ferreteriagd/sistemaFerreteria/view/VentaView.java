package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.VentaController;
import com.ferreteriagd.sistemaFerreteria.controller.ClienteController;
import com.ferreteriagd.sistemaFerreteria.controller.ProductController;
import com.ferreteriagd.sistemaFerreteria.controller.UsuarioController;
import com.ferreteriagd.sistemaFerreteria.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VentaView {
    private Scanner scanner;
    private VentaController ventaController;
    private ClienteController clienteController;
    private ProductController productController;
    private UsuarioController usuarioController;

    public VentaView() {
        this.scanner = new Scanner(System.in);

        this.usuarioController = new UsuarioController();
        this.ventaController = new VentaController();
        this.clienteController = new ClienteController();
        this.productController = new ProductController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║              GESTIÓN DE VENTAS                ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Venta                               ║");
            System.out.println("║  2. Listar Ventas                             ║");
            System.out.println("║  3. Buscar Venta                              ║");
            System.out.println("║  4. Anular Venta                              ║");
            System.out.println("║  5. Reporte de Ventas del Día                 ║");
            System.out.println("║  6. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearVenta();
                        break;
                    case 2:
                        listarVentas();
                        break;
                    case 3:
                        buscarVenta();
                        break;
                    case 4:
                        anularVenta();
                        break;
                    case 5:
                        reporteVentasDelDia();
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

    private void crearVenta() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("             CREAR VENTA               ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del cliente (0 para venta sin cliente): ");
            Long idCliente = Long.parseLong(scanner.nextLine().trim());
            if (idCliente != null && idCliente == 0) idCliente = null;

            // Select seller
            List<Usuario> usuarios = usuarioController.obtenerTodosLosUsuarios();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            System.out.println("\nUsuarios disponibles:");
            for (Usuario usuario : usuarios) {
                System.out.println(usuario.getId() + " - " + usuario.getUsername());
            }

            System.out.print("Seleccione el ID del usuario vendedor: ");
            Long idUsuario = Long.parseLong(scanner.nextLine().trim());

            // Create sale with explicit seller id
            Venta venta = ventaController.crearVenta(idCliente, idUsuario);
            if (venta == null) {
                System.out.println("\nError al crear la venta (usuario inválido o sin permisos).");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            // Add products in-memory using agregarProducto
            BigDecimal totalVenta = BigDecimal.ZERO;
            boolean agregarMasProductos = true;

            while (agregarMasProductos) {
                System.out.print("\nIngrese el ID del producto: ");
                Long idProducto = Long.parseLong(scanner.nextLine().trim());

                Producto producto = productController.buscarPorId(idProducto);
                if (producto == null) {
                    System.out.println("Producto no encontrado.");
                    continue;
                }

                System.out.println("Producto: " + producto.getNombre());
                System.out.println("Precio: $" + producto.getPrecioVenta());
                System.out.println("Stock disponible: " + producto.getStock());

                System.out.print("Cantidad a vender: ");
                int cantidad = Integer.parseInt(scanner.nextLine().trim());

                if (cantidad > producto.getStock()) {
                    System.out.println("Cantidad insuficiente en stock.");
                    continue;
                }

                boolean added = ventaController.agregarProducto(venta, idProducto, cantidad, producto.getPrecioVenta());
                if (!added) {
                    System.out.println("No se pudo agregar el producto.");
                    continue;
                }

                BigDecimal subtotal = producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));
                totalVenta = totalVenta.add(subtotal);

                System.out.println("Subtotal: $" + subtotal);
                System.out.print("¿Desea agregar otro producto? (S/N): ");
                String respuesta = scanner.nextLine().trim().toUpperCase();
                agregarMasProductos = respuesta.equals("S") || respuesta.equals("SI");
            }

            if (venta.getDetalles().isEmpty()) {
                System.out.println("No se agregaron productos a la venta.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            // Persist the sale and update stock
            System.out.print("\nMétodo de pago (enter para 'Efectivo'): ");
            String metodo = scanner.nextLine().trim();
            if (metodo.isEmpty()) metodo = "Efectivo";

            boolean completed = ventaController.completarVenta(venta, metodo, "");
            if (completed) {
                System.out.println("\n✓ Venta creada y guardada exitosamente!");
                System.out.println("Número de venta: " + venta.getNumeroVenta());
                System.out.println("Total de la venta: $" + venta.getTotal());
            } else {
                System.out.println("\nError al crear la venta.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarVentas() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            LISTA DE VENTAS            ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Venta> ventas = ventaController.obtenerTodasLasVentas();

            if (ventas.isEmpty()) {
                System.out.println("No hay ventas registradas.");
            } else {
                System.out.printf("%-8s %-15s %-12s %-12s %-20s %-12s %-12s%n",
                    "ID", "NÚMERO", "CLIENTE", "USUARIO", "FECHA", "TOTAL", "ESTADO");
                System.out.println("─".repeat(95));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (Venta venta : ventas) {
                    System.out.printf("%-8d %-15s %-12s %-12s %-20s $%-11.2f %-12s%n",
                        venta.getId(),
                        venta.getNumeroVenta(),
                        venta.getCliente() != null ? venta.getCliente().getNombre() : "N/A",
                        venta.getUsuario() != null ? venta.getUsuario().getUsername() : "N/A",
                        venta.getFechaVenta().format(formatter),
                        venta.getTotal(),
                        venta.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar las ventas: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarVenta() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("             BUSCAR VENTA              ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por número de venta");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            Venta venta = null;

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID de la venta: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    venta = ventaController.buscarPorId(id);
                    break;
                case 2:
                    System.out.print("Ingrese el número de venta: ");
                    String numeroVenta = scanner.nextLine().trim();
                    venta = ventaController.buscarPorNumero(numeroVenta);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            if (venta != null) {
                mostrarDetalleVenta(venta);
            } else {
                System.out.println("Venta no encontrada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void anularVenta() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            ANULAR VENTA               ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID de la venta a anular: ");
            Long id = Long.parseLong(scanner.nextLine());

            Venta venta = ventaController.buscarPorId(id);
            if (venta == null) {
                System.out.println("Venta no encontrada.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            if (venta.getEstado() == Estado.CANCELADA) {
                System.out.println("La venta ya está cancelada.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleVenta(venta);
            System.out.print("\n¿Está seguro de que desea anular esta venta? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                System.out.print("Motivo de la cancelación: ");
                String motivo = scanner.nextLine().trim();

                if (ventaController.cancelarVenta(id, motivo)) {
                    System.out.println("✓ Venta cancelada exitosamente!");
                } else {
                    System.out.println("Error al cancelar la venta.");
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

    private void reporteVentasDelDia() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("        REPORTE VENTAS DEL DÍA         ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Venta> ventasHoy = ventaController.obtenerVentasDelDia();

            if (ventasHoy.isEmpty()) {
                System.out.println("No hay ventas registradas para el día de hoy.");
            } else {
                BigDecimal totalDelDia = BigDecimal.ZERO;
                int cantidadVentas = 0;

                System.out.printf("%-8s %-15s %-12s %-20s %-12s %-12s%n",
                    "ID", "NÚMERO", "CLIENTE", "FECHA", "TOTAL", "ESTADO");
                System.out.println("─".repeat(85));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (Venta venta : ventasHoy) {
                    if (venta.getEstado() == Estado.COMPLETADA) {
                        totalDelDia = totalDelDia.add(venta.getTotal());
                        cantidadVentas++;
                    }

                    System.out.printf("%-8d %-15s %-12s %-20s $%-11.2f %-12s%n",
                        venta.getId(),
                        venta.getNumeroVenta(),
                        venta.getCliente() != null ? venta.getCliente().getNombre() : "N/A",
                        venta.getFechaVenta().format(formatter),
                        venta.getTotal(),
                        venta.getEstado());
                }

                System.out.println("─".repeat(85));
                System.out.println("Total de ventas: " + cantidadVentas);
                System.out.println("Ingresos del día: $" + totalDelDia);
            }
        } catch (Exception e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleVenta(Venta venta) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("ID Venta: " + venta.getId());
        System.out.println("Número de Venta: " + venta.getNumeroVenta());
        System.out.println("Cliente: " + (venta.getCliente() != null ?
            venta.getCliente().getNombre() + " " + venta.getCliente().getApellido() : "Sin cliente"));
        System.out.println("Usuario: " + (venta.getUsuario() != null ? venta.getUsuario().getUsername() : "N/A"));
        System.out.println("Fecha: " + venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Total: $" + venta.getTotal());
        System.out.println("Estado: " + venta.getEstado());

        // Mostrar detalles de productos
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            System.out.println("\nDetalle de productos:");
            System.out.printf("%-30s %-8s %-12s %-12s%n", "PRODUCTO", "CANT.", "P.UNIT.", "SUBTOTAL");
            System.out.println("-".repeat(60));
            for (DetalleVenta detalle : venta.getDetalles()) {
                System.out.printf("%-30s %-8d $%-11.2f $%-11.2f%n",
                    detalle.getProducto().getNombre().length() > 30 ?
                        detalle.getProducto().getNombre().substring(0, 27) + "..." :
                        detalle.getProducto().getNombre(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal());
            }
        }
        System.out.println("═".repeat(60));
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
