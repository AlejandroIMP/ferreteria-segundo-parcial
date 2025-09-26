package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.VentaController;
import com.ferreteriagd.sistemaFerreteria.model.Venta;
import com.ferreteriagd.sistemaFerreteria.model.Estado;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HistorialVentasView {
    private Scanner scanner;
    private VentaController ventaController;

    public HistorialVentasView() {
        this.scanner = new Scanner(System.in);
        this.ventaController = new VentaController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║           HISTORIAL DE VENTAS                 ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Ver Todas las Ventas                      ║");
            System.out.println("║  2. Ventas por Fecha                          ║");
            System.out.println("║  3. Ventas por Rango de Fechas                ║");
            System.out.println("║  4. Ventas por Cliente                        ║");
            System.out.println("║  5. Reporte Mensual                           ║");
            System.out.println("║  6. Reporte Anual                             ║");
            System.out.println("║  7. Ventas Anuladas                           ║");
            System.out.println("║  8. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        verTodasLasVentas();
                        break;
                    case 2:
                        ventasPorFecha();
                        break;
                    case 3:
                        ventasPorRangoFechas();
                        break;
                    case 4:
                        ventasPorCliente();
                        break;
                    case 5:
                        reporteMensual();
                        break;
                    case 6:
                        reporteAnual();
                        break;
                    case 7:
                        ventasAnuladas();
                        break;
                    case 8:
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

    private void verTodasLasVentas() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          TODAS LAS VENTAS             ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Venta> ventas = ventaController.obtenerTodasLasVentas();
            mostrarListaVentas(ventas);
        } catch (Exception e) {
            System.out.println("✗ Error al cargar las ventas: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void ventasPorFecha() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           VENTAS POR FECHA            ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese la fecha (dd/MM/yyyy) o presione Enter para hoy: ");
            String fechaStr = scanner.nextLine().trim();

            LocalDate fecha;
            if (fechaStr.isEmpty()) {
                fecha = LocalDate.now();
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fecha = LocalDate.parse(fechaStr, formatter);
            }

            // Convertir a LocalDateTime para el inicio y fin del día
            LocalDateTime fechaInicio = fecha.atStartOfDay();
            LocalDateTime fechaFin = fecha.atTime(23, 59, 59);

            List<Venta> ventas = ventaController.obtenerVentasPorFechas(fechaInicio, fechaFin);
            System.out.println("Ventas del " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");
            mostrarListaVentas(ventas);

        } catch (DateTimeParseException e) {
            System.out.println("✗ Error: Formato de fecha inválido. Use dd/MM/yyyy");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void ventasPorRangoFechas() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("       VENTAS POR RANGO DE FECHAS      ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Fecha inicial (dd/MM/yyyy): ");
            String fechaInicialStr = scanner.nextLine().trim();

            System.out.print("Fecha final (dd/MM/yyyy): ");
            String fechaFinalStr = scanner.nextLine().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaInicial = LocalDate.parse(fechaInicialStr, formatter);
            LocalDate fechaFinal = LocalDate.parse(fechaFinalStr, formatter);

            if (fechaInicial.isAfter(fechaFinal)) {
                System.out.println("✗ Error: La fecha inicial no puede ser posterior a la fecha final.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            // Convertir a LocalDateTime
            LocalDateTime fechaInicioDateTime = fechaInicial.atStartOfDay();
            LocalDateTime fechaFinDateTime = fechaFinal.atTime(23, 59, 59);

            List<Venta> ventas = ventaController.obtenerVentasPorFechas(fechaInicioDateTime, fechaFinDateTime);
            System.out.println("Ventas del " + fechaInicial.format(formatter) + " al " + fechaFinal.format(formatter) + ":");
            mostrarListaVentas(ventas);

        } catch (DateTimeParseException e) {
            System.out.println("✗ Error: Formato de fecha inválido. Use dd/MM/yyyy");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void ventasPorCliente() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          VENTAS POR CLIENTE           ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del cliente: ");
            Long idCliente = Long.parseLong(scanner.nextLine().trim());

            List<Venta> ventas = ventaController.obtenerVentasPorCliente(idCliente);
            System.out.println("Ventas del cliente ID " + idCliente + ":");
            mostrarListaVentas(ventas);

        } catch (NumberFormatException e) {
            System.out.println("✗ Error: Por favor, ingrese un número válido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void reporteMensual() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           REPORTE MENSUAL             ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el mes (1-12): ");
            int mes = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Ingrese el año: ");
            int anio = Integer.parseInt(scanner.nextLine().trim());

            if (mes < 1 || mes > 12) {
                System.out.println("✗ Error: El mes debe estar entre 1 y 12.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            LocalDate inicioMes = LocalDate.of(anio, mes, 1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

            // Convertir a LocalDateTime
            LocalDateTime fechaInicioDateTime = inicioMes.atStartOfDay();
            LocalDateTime fechaFinDateTime = finMes.atTime(23, 59, 59);

            List<Venta> ventas = ventaController.obtenerVentasPorFechas(fechaInicioDateTime, fechaFinDateTime);

            System.out.println("Reporte de " + inicioMes.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ":");
            mostrarResumenVentas(ventas);

        } catch (NumberFormatException e) {
            System.out.println("✗ Error: Por favor, ingrese números válidos.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void reporteAnual() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("            REPORTE ANUAL              ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el año: ");
            int anio = Integer.parseInt(scanner.nextLine().trim());

            LocalDate inicioAnio = LocalDate.of(anio, 1, 1);
            LocalDate finAnio = LocalDate.of(anio, 12, 31);

            // Convertir a LocalDateTime
            LocalDateTime fechaInicioDateTime = inicioAnio.atStartOfDay();
            LocalDateTime fechaFinDateTime = finAnio.atTime(23, 59, 59);

            List<Venta> ventas = ventaController.obtenerVentasPorFechas(fechaInicioDateTime, fechaFinDateTime);

            System.out.println("Reporte del año " + anio + ":");
            mostrarResumenVentas(ventas);

        } catch (NumberFormatException e) {
            System.out.println("✗ Error: Por favor, ingrese un año válido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void ventasAnuladas() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           VENTAS CANCELADAS           ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Venta> todasLasVentas = ventaController.obtenerTodasLasVentas();
            List<Venta> ventasCanceladas = todasLasVentas.stream()
                .filter(venta -> venta.getEstado() == Estado.CANCELADA)
                .toList();

            mostrarListaVentas(ventasCanceladas);

        } catch (Exception e) {
            System.out.println("✗ Error al cargar las ventas canceladas: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarListaVentas(List<Venta> ventas) {
        if (ventas.isEmpty()) {
            System.out.println("No se encontraron ventas.");
            return;
        }

        System.out.printf("%-8s %-12s %-20s %-20s %-12s %-12s%n",
            "ID", "CLIENTE", "VENDEDOR", "FECHA", "TOTAL", "ESTADO");
        System.out.println("─".repeat(90));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        BigDecimal totalGeneral = BigDecimal.ZERO;
        int ventasCompletadas = 0;

        for (Venta venta : ventas) {
            String clienteInfo = venta.getCliente() != null ?
                venta.getCliente().getNombre() : "Sin cliente";
            String vendedorInfo = venta.getUsuario() != null ?
                venta.getUsuario().getUsername() : "N/A";

            System.out.printf("%-8d %-12s %-20s %-20s $%-11.2f %-12s%n",
                venta.getId(),
                clienteInfo.length() > 12 ? clienteInfo.substring(0, 9) + "..." : clienteInfo,
                vendedorInfo.length() > 20 ? vendedorInfo.substring(0, 17) + "..." : vendedorInfo,
                venta.getFechaVenta().format(formatter),
                venta.getTotal(),
                venta.getEstado().getNombre());

            if (venta.getEstado() == Estado.COMPLETADA) {
                totalGeneral = totalGeneral.add(venta.getTotal());
                ventasCompletadas++;
            }
        }

        System.out.println("─".repeat(90));
        System.out.println("Total de registros: " + ventas.size());
        System.out.println("Ventas completadas: " + ventasCompletadas);
        System.out.println("Total en ventas completadas: $" + totalGeneral);
    }

    private void mostrarResumenVentas(List<Venta> ventas) {
        if (ventas.isEmpty()) {
            System.out.println("No se encontraron ventas para el período seleccionado.");
            return;
        }

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalCanceladas = BigDecimal.ZERO;
        int ventasCompletadas = 0;
        int ventasCanceladas = 0;

        for (Venta venta : ventas) {
            if (venta.getEstado() == Estado.COMPLETADA) {
                totalVentas = totalVentas.add(venta.getTotal());
                ventasCompletadas++;
            } else if (venta.getEstado() == Estado.CANCELADA) {
                totalCanceladas = totalCanceladas.add(venta.getTotal());
                ventasCanceladas++;
            }
        }

        System.out.println("═".repeat(50));
        System.out.println("RESUMEN DEL PERÍODO");
        System.out.println("═".repeat(50));
        System.out.println("Total de transacciones: " + ventas.size());
        System.out.println("Ventas completadas: " + ventasCompletadas);
        System.out.println("Ventas canceladas: " + ventasCanceladas);
        System.out.println("Ingresos totales: $" + String.format("%.2f", totalVentas));
        System.out.println("Ventas perdidas (canceladas): $" + String.format("%.2f", totalCanceladas));

        if (ventasCompletadas > 0) {
            BigDecimal promedioVenta = totalVentas.divide(BigDecimal.valueOf(ventasCompletadas), 2, BigDecimal.ROUND_HALF_UP);
            System.out.println("Promedio por venta: $" + String.format("%.2f", promedioVenta));
        }
        System.out.println("═".repeat(50));

        // Mostrar lista detallada
        System.out.println("\nDetalle de ventas:");
        mostrarListaVentas(ventas);
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
