package com.ferreteriagd.sistemaFerreteria.view;

import com.ferreteriagd.sistemaFerreteria.controller.ProductController;
import com.ferreteriagd.sistemaFerreteria.controller.ProveedorController;
import com.ferreteriagd.sistemaFerreteria.model.Producto;
import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ProductView {
    private Scanner scanner;
    private ProductController productController;
    private ProveedorController proveedorController;

    public ProductView() {
        this.scanner = new Scanner(System.in);
        this.productController = new ProductController();
        this.proveedorController = new ProveedorController();
    }

    public void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
            limpiarPantalla();
            System.out.println("╔═══════════════════════════════════════════════╗");
            System.out.println("║             GESTIÓN DE PRODUCTOS             ║");
            System.out.println("╠═══════════════════════════════════════════════╣");
            System.out.println("║  1. Crear Producto                            ║");
            System.out.println("║  2. Listar Productos                          ║");
            System.out.println("║  3. Buscar Producto                           ║");
            System.out.println("║  4. Actualizar Producto                       ║");
            System.out.println("║  5. Eliminar Producto                         ║");
            System.out.println("║  6. Control de Inventario                     ║");
            System.out.println("║  7. Productos con Stock Bajo                  ║");
            System.out.println("║  8. Volver al Menú Principal                  ║");
            System.out.println("╚═══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        crearProducto();
                        break;
                    case 2:
                        listarProductos();
                        break;
                    case 3:
                        buscarProducto();
                        break;
                    case 4:
                        actualizarProducto();
                        break;
                    case 5:
                        eliminarProducto();
                        break;
                    case 6:
                        controlInventario();
                        break;
                    case 7:
                        productosStockBajo();
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

    private void crearProducto() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           CREAR PRODUCTO              ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Código del producto: ");
            String codigo = scanner.nextLine().trim();

            System.out.print("Nombre del producto: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            System.out.print("Categoría: ");
            String categoria = scanner.nextLine().trim();

            System.out.print("Marca: ");
            String marca = scanner.nextLine().trim();

            System.out.print("Modelo: ");
            String modelo = scanner.nextLine().trim();

            System.out.print("Precio de compra: ");
            BigDecimal precioCompra = new BigDecimal(scanner.nextLine().trim());

            System.out.print("Precio de venta: ");
            BigDecimal precioVenta = new BigDecimal(scanner.nextLine().trim());

            System.out.print("Stock mínimo: ");
            Integer stockMinimo = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Unidad de medida: ");
            String unidadMedida = scanner.nextLine().trim();

            // Mostrar proveedores disponibles
            List<Proveedor> proveedores = proveedorController.obtenerTodosLosProveedores();
            Long proveedorId = null;

            if (!proveedores.isEmpty()) {
                System.out.println("\nProveedores disponibles:");
                for (Proveedor proveedor : proveedores) {
                    System.out.println(proveedor.getId() + " - " + proveedor.getNombre());
                }

                System.out.print("Seleccione el ID del proveedor (0 para ninguno): ");
                long selectedId = Long.parseLong(scanner.nextLine().trim());
                if (selectedId > 0) {
                    proveedorId = selectedId;
                }
            } else {
                System.out.println("No hay proveedores registrados. El producto se creará sin proveedor.");
            }

            if (productController.crearProducto(codigo, nombre, descripcion, categoria, marca, modelo,
                                              precioCompra, precioVenta, stockMinimo, unidadMedida, proveedorId)) {
                System.out.println("✓ Producto creado exitosamente!");
            } else {
                System.out.println("Error al crear el producto. Puede que el código ya exista.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void listarProductos() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           LISTA DE PRODUCTOS          ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Producto> productos = productController.obtenerTodosLosProductos();

            if (productos.isEmpty()) {
                System.out.println("No hay productos registrados.");
            } else {
                System.out.printf("%-5s %-15s %-25s %-15s %-15s %-8s %-8s%n",
                    "ID", "CÓDIGO", "NOMBRE", "P.COMPRA", "P.VENTA", "STOCK", "MIN");
                System.out.println("─".repeat(100));

                for (Producto producto : productos) {
                    System.out.printf("%-5d %-15s %-25s $%-14.2f $%-14.2f %-8d %-8d%n",
                        producto.getId(),
                        producto.getCodigo() != null ? producto.getCodigo() : "",
                        producto.getNombre().length() > 25 ? producto.getNombre().substring(0, 22) + "..." : producto.getNombre(),
                        producto.getPrecioCompra(),
                        producto.getPrecioVenta(),
                        producto.getStock(),
                        producto.getStockMinimo());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los productos: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void buscarProducto() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("           BUSCAR PRODUCTO             ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por código");
        System.out.println("3. Buscar por nombre");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el ID del producto: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    Producto producto = productController.buscarPorId(id);
                    if (producto != null) {
                        mostrarDetalleProducto(producto);
                    } else {
                        System.out.println("Producto no encontrado.");
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el código del producto: ");
                    String codigo = scanner.nextLine().trim();
                    Producto productoPorCodigo = productController.buscarPorCodigo(codigo);
                    if (productoPorCodigo != null) {
                        mostrarDetalleProducto(productoPorCodigo);
                    } else {
                        System.out.println("Producto no encontrado.");
                    }
                    break;
                case 3:
                    System.out.print("Ingrese el nombre del producto: ");
                    String nombre = scanner.nextLine().trim();
                    List<Producto> productos = productController.buscarPorNombre(nombre);
                    if (productos != null && !productos.isEmpty()) {
                        System.out.println("\nResultados encontrados:");
                        System.out.printf("%-5s %-15s %-25s %-15s %-15s %-8s%n",
                            "ID", "CÓDIGO", "NOMBRE", "P.COMPRA", "P.VENTA", "STOCK");
                        System.out.println("─".repeat(90));
                        for (Producto p : productos) {
                            System.out.printf("%-5d %-15s %-25s $%-14.2f $%-14.2f %-8d%n",
                                p.getId(),
                                p.getCodigo() != null ? p.getCodigo() : "",
                                p.getNombre().length() > 25 ? p.getNombre().substring(0, 22) + "..." : p.getNombre(),
                                p.getPrecioCompra(),
                                p.getPrecioVenta(),
                                p.getStock());
                        }
                    } else {
                        System.out.println("No se encontraron productos con ese nombre.");
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

    private void actualizarProducto() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         ACTUALIZAR PRODUCTO           ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del producto a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Producto producto = productController.buscarPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleProducto(producto);
            System.out.println("\nIngrese los nuevos datos (deje vacío para mantener el valor actual):");

            System.out.print("Nombre [" + producto.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = producto.getNombre();

            System.out.print("Descripción [" + (producto.getDescripcion() != null ? producto.getDescripcion() : "") + "]: ");
            String descripcion = scanner.nextLine().trim();
            if (descripcion.isEmpty()) descripcion = producto.getDescripcion();

            System.out.print("Categoría [" + (producto.getCategoria() != null ? producto.getCategoria() : "") + "]: ");
            String categoria = scanner.nextLine().trim();
            if (categoria.isEmpty()) categoria = producto.getCategoria();

            System.out.print("Marca [" + (producto.getMarca() != null ? producto.getMarca() : "") + "]: ");
            String marca = scanner.nextLine().trim();
            if (marca.isEmpty()) marca = producto.getMarca();

            System.out.print("Modelo [" + (producto.getModelo() != null ? producto.getModelo() : "") + "]: ");
            String modelo = scanner.nextLine().trim();
            if (modelo.isEmpty()) modelo = producto.getModelo();

            System.out.print("Precio de compra [" + producto.getPrecioCompra() + "]: ");
            String precioCompraStr = scanner.nextLine().trim();
            BigDecimal precioCompra = precioCompraStr.isEmpty() ? producto.getPrecioCompra() : new BigDecimal(precioCompraStr);

            System.out.print("Precio de venta [" + producto.getPrecioVenta() + "]: ");
            String precioVentaStr = scanner.nextLine().trim();
            BigDecimal precioVenta = precioVentaStr.isEmpty() ? producto.getPrecioVenta() : new BigDecimal(precioVentaStr);

            System.out.print("Stock mínimo [" + producto.getStockMinimo() + "]: ");
            String stockMinimoStr = scanner.nextLine().trim();
            Integer stockMinimo = stockMinimoStr.isEmpty() ? producto.getStockMinimo() : Integer.parseInt(stockMinimoStr);

            System.out.print("Unidad de medida [" + (producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "") + "]: ");
            String unidadMedida = scanner.nextLine().trim();
            if (unidadMedida.isEmpty()) unidadMedida = producto.getUnidadMedida();

            // Proveedor
            Long proveedorId = null;
            if (producto.getProveedor() != null) {
                proveedorId = producto.getProveedor().getId();
            }

            if (productController.actualizarProducto(id, nombre, descripcion, categoria, marca, modelo,
                                                   precioCompra, precioVenta, stockMinimo, unidadMedida, proveedorId)) {
                System.out.println("✓ Producto actualizado exitosamente!");
            } else {
                System.out.println("Error al actualizar el producto.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese valores numéricos válidos.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void eliminarProducto() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("          ELIMINAR PRODUCTO            ");
        System.out.println("═══════════════════════════════════════");

        try {
            System.out.print("Ingrese el ID del producto a eliminar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Producto producto = productController.buscarPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return;
            }

            mostrarDetalleProducto(producto);
            System.out.print("\n¿Está seguro de que desea eliminar este producto? (S/N): ");
            String confirmacion = scanner.nextLine().trim().toUpperCase();

            if (confirmacion.equals("S") || confirmacion.equals("SI")) {
                if (productController.eliminarProducto(id)) {
                    System.out.println("✓ Producto eliminado exitosamente!");
                } else {
                    System.out.println("Error al eliminar el producto.");
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

    private void controlInventario() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("         CONTROL DE INVENTARIO         ");
        System.out.println("═══════════════════════════════════════");

        System.out.println("1. Aumentar stock");
        System.out.println("2. Reducir stock");
        System.out.print("Seleccione una opción: ");

        try {
            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    gestionarStock(true);
                    break;
                case 2:
                    gestionarStock(false);
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void gestionarStock(boolean aumentar) {
        System.out.print("Ingrese el ID del producto: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

            Producto producto = productController.buscarPorId(id);
            if (producto == null) {
                System.out.println("Producto no encontrado.");
                return;
            }

            mostrarDetalleProducto(producto);
            System.out.print("Ingrese la cantidad a " + (aumentar ? "aumentar" : "reducir") + ": ");
            Integer cantidad = Integer.parseInt(scanner.nextLine());

            boolean resultado;
            if (aumentar) {
                resultado = productController.aumentarStock(id, cantidad);
            } else {
                resultado = productController.reducirStock(id, cantidad);
            }

            if (resultado) {
                System.out.println("✓ Stock " + (aumentar ? "aumentado" : "reducido") + " exitosamente!");
            } else {
                System.out.println("Error al " + (aumentar ? "aumentar" : "reducir") + " el stock.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese números válidos.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void productosStockBajo() {
        limpiarPantalla();
        System.out.println("═══════════════════════════════════════");
        System.out.println("        PRODUCTOS CON STOCK BAJO       ");
        System.out.println("═══════════════════════════════════════");

        try {
            List<Producto> productos = productController.obtenerProductosConStockBajo();

            if (productos.isEmpty()) {
                System.out.println("No hay productos con stock bajo.");
            } else {
                System.out.printf("%-5s %-15s %-25s %-8s %-10s%n",
                    "ID", "CÓDIGO", "NOMBRE", "STOCK", "MIN");
                System.out.println("─".repeat(70));

                for (Producto producto : productos) {
                    System.out.printf("%-5d %-15s %-25s %-8d %-10d%n",
                        producto.getId(),
                        producto.getCodigo() != null ? producto.getCodigo() : "",
                        producto.getNombre().length() > 25 ? producto.getNombre().substring(0, 22) + "..." : producto.getNombre(),
                        producto.getStock(),
                        producto.getStockMinimo());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los productos: " + e.getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void mostrarDetalleProducto(Producto producto) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("ID: " + producto.getId());
        System.out.println("Código: " + (producto.getCodigo() != null ? producto.getCodigo() : ""));
        System.out.println("Nombre: " + producto.getNombre());
        System.out.println("Descripción: " + (producto.getDescripcion() != null ? producto.getDescripcion() : ""));
        System.out.println("Categoría: " + (producto.getCategoria() != null ? producto.getCategoria() : ""));
        System.out.println("Marca: " + (producto.getMarca() != null ? producto.getMarca() : ""));
        System.out.println("Modelo: " + (producto.getModelo() != null ? producto.getModelo() : ""));
        System.out.println("Precio de compra: $" + producto.getPrecioCompra());
        System.out.println("Precio de venta: $" + producto.getPrecioVenta());
        System.out.println("Stock actual: " + producto.getStock());
        System.out.println("Stock mínimo: " + producto.getStockMinimo());
        System.out.println("Unidad de medida: " + (producto.getUnidadMedida() != null ? producto.getUnidadMedida() : ""));
        System.out.println("Proveedor: " + (producto.getProveedor() != null ? producto.getProveedor().getNombre() : "Sin proveedor"));
        System.out.println("Activo: " + (producto.isActivo() ? "Sí" : "No"));
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
