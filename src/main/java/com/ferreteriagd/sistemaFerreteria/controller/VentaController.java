package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.*;
import com.ferreteriagd.sistemaFerreteria.dao.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentaController {
    private final VentaDAO ventaDAO;
    private final ProductDAO productDAO;
    private final ClienteDAO clienteDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioController usuarioController;

    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.productDAO = new ProductDAO();
        this.clienteDAO = new ClienteDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioController = new UsuarioController();
    }

    // Constructor alternativo que acepta directamente los DAOs
    public VentaController(UsuarioController usuarioController) {
        this.ventaDAO = new VentaDAO();
        this.productDAO = new ProductDAO();
        this.clienteDAO = new ClienteDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioController = usuarioController;
    }

    // Crear nueva venta
    public Venta crearVenta(Long clienteId) {
        try {
            Usuario usuario = usuarioController.getUsuarioActual();
            if (usuario == null || !usuario.getRol().isPuedeVender()) {
                return null; // Usuario sin permisos
            }

            Cliente cliente = null;
            if (clienteId != null) {
                cliente = clienteDAO.buscarPorId(clienteId);
            }

            String numeroVenta = generarNumeroVenta();

            return new Venta(numeroVenta, cliente, usuario);
        } catch (Exception e) {
            System.err.println("Error en VentaController.crearVenta: " + e.getMessage());
            return null;
        }
    }

    public Venta crearVenta(Long clienteId, Long usuarioId) {
        try {
            Usuario usuario = null;
            if (usuarioId != null) {
                usuario = usuarioDAO.buscarPorId(usuarioId);
            }
            if (usuario == null || !usuario.getRol().isPuedeVender()) {
                return null; // no seller or no permissions
            }

            Cliente cliente = null;
            if (clienteId != null) {
                cliente = clienteDAO.buscarPorId(clienteId);
            }

            String numeroVenta = generarNumeroVenta();
            return new Venta(numeroVenta, cliente, usuario);
        } catch (Exception e) {
            System.err.println("Error en VentaController.crearVenta(usuarioId): " + e.getMessage());
            return null;
        }
    }

    // Agregar producto a la venta
    public boolean agregarProducto(Venta venta, Long productoId, Integer cantidad, BigDecimal precioUnitario) {
        try {
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            Producto producto = productDAO.buscarPorId(productoId);
            if (producto == null) {
                return false;
            }

            if (producto.getStock() < cantidad) {
                return false;
            }

            if (precioUnitario == null) {
                precioUnitario = producto.getPrecioVenta();
            }

            DetalleVenta detalle = new DetalleVenta(producto, cantidad, precioUnitario);
            venta.agregarDetalle(detalle);
            return true;
        } catch (Exception e) {
            System.err.println("Error en VentaController.agregarProducto: " + e.getMessage());
            return false;
        }
    }



    // Remover producto de la venta
    public boolean removerProducto(Venta venta, DetalleVenta detalle) {
        try {
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            venta.removerDetalle(detalle);
            return true;
        } catch (Exception e) {
            System.err.println("Error en VentaController.removerProducto: " + e.getMessage());
            return false;
        }
    }

    // Actualizar cantidad de producto en la venta
    public boolean actualizarCantidadProducto(Venta venta, DetalleVenta detalle, Integer nuevaCantidad) {
        try {
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            // Validar stock disponible
            if (detalle.getProducto().getStock() < nuevaCantidad) {
                return false;
            }

            detalle.setCantidad(nuevaCantidad);
            venta.calcularTotales();
            return true;
        } catch (Exception e) {
            System.err.println("Error en VentaController.actualizarCantidadProducto: " + e.getMessage());
            return false;
        }
    }

    // Aplicar descuento a la venta
    public boolean aplicarDescuento(Venta venta, BigDecimal descuento) {
        try {
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            if (descuento.compareTo(BigDecimal.ZERO) >= 0 &&
                descuento.compareTo(venta.getSubtotal()) <= 0) {
                venta.setDescuento(descuento);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en VentaController.aplicarDescuento: " + e.getMessage());
            return false;
        }
    }

    // Completar venta
    public boolean completarVenta(Venta venta, String metodoPago, String observaciones) {
        try {
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            if (venta.getDetalles().isEmpty()) {
                return false; // No se puede completar venta sin productos
            }

            // Validar stock final antes de completar
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productDAO.buscarPorId(detalle.getProducto().getId());
                if (producto == null || producto.getStock() < detalle.getCantidad()) {
                    return false;
                }
            }

            venta.setMetodoPago(metodoPago);
            venta.setObservaciones(observaciones);
            venta.completarVenta();

            // Guardar la venta en la base de datos
            if (ventaDAO.crear(venta)) {
                // update stock in DB
                for (DetalleVenta detalle : venta.getDetalles()) {
                    Producto producto = detalle.getProducto();
                    int nuevoStock = producto.getStock() - detalle.getCantidad();
                    productDAO.actualizarStock(producto.getId(), nuevoStock);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en VentaController.completarVenta: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelarVenta(Long ventaId, String motivo) {
        try {
            Venta venta = ventaDAO.buscarPorId(ventaId);
            if (venta == null) {
                return false;
            }
            if (venta.getEstado() == Estado.CANCELADA) {
                return false; // already cancelled
            }

            String existingObs = venta.getObservaciones() != null ? venta.getObservaciones() : "";
            String appended = existingObs.isEmpty() ? motivo : existingObs + " | " + motivo;

            return ventaDAO.actualizarEstadoConObservaciones(ventaId, Estado.CANCELADA, appended);
        } catch (Exception e) {
            System.err.println("Error en VentaController.cancelarVenta: " + e.getMessage());
            return false;
        }
    }

    // Buscar venta por ID
    public Venta buscarPorId(Long id) {
        return ventaDAO.buscarPorId(id);
    }

    // Buscar venta por número
    public Venta buscarPorNumero(String numeroVenta) {
        try {
            return ventaDAO.buscarPorNumero(numeroVenta);
        } catch (Exception e) {
            System.err.println("Error en VentaController.buscarPorNumero: " + e.getMessage());
            return null;
        }
    }

    // Obtener ventas por cliente
    public List<Venta> obtenerVentasPorCliente(Long clienteId) {
        try {
            return ventaDAO.obtenerVentasPorCliente(clienteId);
        } catch (Exception e) {
            System.err.println("Error en VentaController.obtenerVentasPorCliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener ventas por usuario
    public List<Venta> obtenerVentasPorUsuario(Long usuarioId) {
        try {
            return ventaDAO.obtenerVentasPorUsuario(usuarioId);
        } catch (Exception e) {
            System.err.println("Error en VentaController.obtenerVentasPorUsuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener ventas por estado
    public List<Venta> obtenerVentasPorEstado(Estado estado) {
        try {
            return ventaDAO.obtenerVentasPorEstado(estado);
        } catch (Exception e) {
            System.err.println("Error en VentaController.obtenerVentasPorEstado: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener ventas del día
    public List<Venta> obtenerVentasDelDia() {
        return ventaDAO.obtenerVentasDelDia();
    }

    // Obtener ventas por rango de fechas
    public List<Venta> obtenerVentasPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaDAO.obtenerVentasPorFechas(fechaInicio, fechaFin);
    }

    // Obtener todas las ventas
    public List<Venta> obtenerTodasLasVentas() {
        try {
            return ventaDAO.listarTodos();
        } catch (Exception e) {
            System.err.println("Error in VentaController.obtenerTodasLasVentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Estadísticas de ventas
    public BigDecimal calcularVentasDelDia() {
        return ventaDAO.calcularVentasDelDia();
    }

    public BigDecimal calcularVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaDAO.obtenerVentasPorFechas(fechaInicio, fechaFin).stream()
                .filter(v -> v.getEstado() == Estado.COMPLETADA)
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int contarVentasDelDia() {
        return (int) ventaDAO.obtenerVentasDelDia().stream()
                .filter(v -> v.getEstado() == Estado.COMPLETADA)
                .count();
    }

    public int contarVentasPorEstado(Estado estado) {
        return (int) ventaDAO.obtenerVentasPorEstado(estado).stream()
                .filter(v -> v.getEstado() == estado)
                .count();
    }

    // Productos más vendidos
    public List<Producto> obtenerProductosMasVendidos(int limite) {
        try {
            if (limite <= 0) return null;
            return ventaDAO.obtenerProductosMasVendidos(limite);

        } catch (Exception e) {
            System.err.println("Error en VentaController.obtenerProductosMasVendidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Clientes con más compras
    public List<Cliente> obtenerClientesConMasCompras(int limite) {
        return ventaDAO.obtenerClientesConMasCompras(limite);
    }

    // Generar número de venta único
    private String generarNumeroVenta() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String secuencia = String.format("%04d", System.currentTimeMillis() % 10000);
        return "V" + fecha + "-" + secuencia;
    }

    // Validar si se puede modificar la venta
    public boolean puedeModificarVenta(Long ventaId) {
        try{
            Venta venta = ventaDAO.buscarPorId(ventaId);
            return venta != null && venta.getEstado() == Estado.PENDIENTE;
        }
        catch (Exception e) {
            System.err.println("Error en VentaController.puedeModificarVenta: " + e.getMessage());
            return false;
        }
    }

    // Obtener resumen de venta
    public String obtenerResumenVenta(Long ventaId) {
        Venta venta = ventaDAO.buscarPorId(ventaId);
        if (venta == null) {
            return null;
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("=== VENTA ").append(venta.getNumeroVenta()).append(" ===\n");
        resumen.append("Fecha: ").append(venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        resumen.append("Vendedor: ").append(venta.getUsuario().getNombreCompleto()).append("\n");

        if (venta.getCliente() != null) {
            resumen.append("Cliente: ").append(venta.getCliente().getNombreCompleto()).append("\n");
        }

        resumen.append("Estado: ").append(venta.getEstado().getNombre()).append("\n");
        resumen.append("\n--- PRODUCTOS ---\n");

        for (DetalleVenta detalle : venta.getDetalles()) {
            resumen.append(detalle.getProducto().getNombre())
                   .append(" x").append(detalle.getCantidad())
                   .append(" @ $").append(detalle.getPrecioUnitario())
                   .append(" = $").append(detalle.getSubtotal()).append("\n");
        }

        resumen.append("\n--- TOTALES ---\n");
        resumen.append("Subtotal: $").append(venta.getSubtotal()).append("\n");
        resumen.append("Impuesto: $").append(venta.getImpuesto()).append("\n");
        if (venta.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            resumen.append("Descuento: $").append(venta.getDescuento()).append("\n");
        }
        resumen.append("TOTAL: $").append(venta.getTotal()).append("\n");

        if (venta.getMetodoPago() != null) {
            resumen.append("Método de pago: ").append(venta.getMetodoPago()).append("\n");
        }

        return resumen.toString();
    }

    public boolean agregarDetalleVenta(Long ventaId, Long productoId, Integer cantidad, BigDecimal precioUnitario) {
        try {
            Venta venta = ventaDAO.buscarPorId(ventaId);
            if (venta == null) {
                return false;
            }
            return agregarProducto(venta, productoId, cantidad, precioUnitario);
        } catch (Exception e) {
            System.err.println("Error en VentaController.agregarDetalleVenta: " + e.getMessage());
            return false;
        }
    }

    public boolean finalizarVenta(Long id) {
        try {
            Venta venta = ventaDAO.buscarPorId(id);
            if (venta == null || venta.getEstado() != Estado.PENDIENTE) {
                return false;
            }

            // Validar que tenga productos
            if (venta.getDetalles().isEmpty()) {
                return false;
            }

            // Finalizar la venta con método de pago predeterminado
            return completarVenta(venta, "Efectivo", "Finalizada automáticamente");
        } catch (Exception e) {
            System.err.println("Error en VentaController.finalizarVenta: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> listarVentas() {
        try {
            return ventaDAO.listarTodos();
        } catch (Exception e) {
            System.err.println("Error en VentaController.listarVentas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Venta> buscarVentasPorFecha(String fechaInicio, String fechaFin) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio + " 00:00:00", formatter);
            LocalDateTime fin = LocalDateTime.parse(fechaFin + " 23:59:59", formatter);
            return ventaDAO.obtenerVentasPorFechas(inicio, fin);
        } catch (Exception e) {
            System.err.println("Error en VentaController.buscarVentasPorFecha: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Venta> buscarVentasPorCliente(String cliente) {
        try {
            List<Venta> allVentas = ventaDAO.listarTodos();
            List<Venta> filteredVentas = new ArrayList<>();

            for (Venta venta : allVentas) {
                if (venta.getCliente() != null && venta.getCliente().getNombreCompleto().toLowerCase().contains(cliente.toLowerCase())) {
                    filteredVentas.add(venta);
                }
            }
            return filteredVentas;
        } catch (Exception e) {
            System.err.println("Error en VentaController.buscarVentasPorCliente: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public Venta obtenerVentaPorId(Long id) {
        try {
            return ventaDAO.buscarPorId(id);
        } catch (Exception e) {
            System.err.println("Error en VentaController.obtenerVentaPorId: " + e.getMessage());
            return null;
        }
    }

}
