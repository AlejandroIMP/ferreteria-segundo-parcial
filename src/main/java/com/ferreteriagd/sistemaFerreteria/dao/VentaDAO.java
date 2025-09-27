package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.*;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Venta
 */
public class VentaDAO {
    private DatabaseManager dbManager;
    private ClienteDAO clienteDAO;
    private UsuarioDAO usuarioDAO;
    private ProductDAO productDAO;

    public VentaDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.clienteDAO = new ClienteDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.productDAO = new ProductDAO();
    }

    public boolean crear(Venta venta) {
        Connection conn = null;
        PreparedStatement stmtVenta = null;
        PreparedStatement stmtDetalle = null;

        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar venta
            String sqlVenta = """
                INSERT INTO ventas (numero_venta, cliente_id, usuario_id, subtotal, impuesto, 
                                  descuento, total, estado, observaciones, metodo_pago)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            stmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            stmtVenta.setString(1, venta.getNumeroVenta());
            
            if (venta.getCliente() != null) {
                stmtVenta.setLong(2, venta.getCliente().getId());
            } else {
                stmtVenta.setNull(2, Types.BIGINT);
            }
            
            stmtVenta.setLong(3, venta.getUsuario().getId());
            stmtVenta.setBigDecimal(4, venta.getSubtotal());
            stmtVenta.setBigDecimal(5, venta.getImpuesto());
            stmtVenta.setBigDecimal(6, venta.getDescuento());
            stmtVenta.setBigDecimal(7, venta.getTotal());
            stmtVenta.setString(8, venta.getEstado().name());
            stmtVenta.setString(9, venta.getObservaciones());
            stmtVenta.setString(10, venta.getMetodoPago());

            int affectedRows = stmtVenta.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Obtener ID generado
            try (ResultSet generatedKeys = stmtVenta.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venta.setId(generatedKeys.getLong(1));
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Insertar detalles de venta
            String sqlDetalle = """
                INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal, observaciones)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            stmtDetalle = conn.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS);
            
            for (DetalleVenta detalle : venta.getDetalles()) {
                stmtDetalle.setLong(1, venta.getId());
                stmtDetalle.setLong(2, detalle.getProducto().getId());
                stmtDetalle.setInt(3, detalle.getCantidad());
                stmtDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                stmtDetalle.setBigDecimal(5, detalle.getSubtotal());
                stmtDetalle.setString(6, detalle.getObservaciones());
                stmtDetalle.addBatch();
            }

            int[] detalleResults = stmtDetalle.executeBatch();
            
            // Verificar que todos los detalles se insertaron correctamente
            for (int result : detalleResults) {
                if (result == Statement.EXECUTE_FAILED) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al crear venta: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autocommit: " + e.getMessage());
            }
            dbManager.closeResources(conn, stmtVenta);
            if (stmtDetalle != null) {
                try { stmtDetalle.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }

    public Venta buscarPorId(Long id) {
        String sql = """
            SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
                   u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM ventas v
            LEFT JOIN clientes c ON v.cliente_id = c.id
            INNER JOIN usuarios u ON v.usuario_id = u.id
            WHERE v.id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Venta venta = mapResultSetToVenta(rs);
                // Cargar detalles de la venta
                cargarDetallesVenta(venta, conn);
                return venta;
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar venta por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Venta> obtenerVentasDelDia() {
        String sql = """
            SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
                   u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM ventas v
            LEFT JOIN clientes c ON v.cliente_id = c.id
            INNER JOIN usuarios u ON v.usuario_id = u.id
            WHERE CAST(v.fecha_venta AS DATE) = CAST(GETDATE() AS DATE)
            ORDER BY v.fecha_venta DESC
            """;

        return ejecutarConsultaVentas(sql);
    }

    public List<Venta> obtenerVentasPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = """
            SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
                   u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
            FROM ventas v
            LEFT JOIN clientes c ON v.cliente_id = c.id
            INNER JOIN usuarios u ON v.usuario_id = u.id
            WHERE v.fecha_venta BETWEEN ? AND ?
            ORDER BY v.fecha_venta DESC
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Venta> ventas = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            rs = stmt.executeQuery();

            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por fechas: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return ventas;
    }

    public boolean actualizarEstado(Long ventaId, Estado nuevoEstado) {
        String sql = "UPDATE ventas SET estado = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nuevoEstado.name());
            stmt.setLong(2, ventaId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de venta: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public BigDecimal calcularVentasDelDia() {
        String sql = """
            SELECT COALESCE(SUM(total), 0) as total_dia 
            FROM ventas 
            WHERE CAST(fecha_venta AS DATE) = CAST(GETDATE() AS DATE) 
            AND estado = 'COMPLETADA'
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_dia");
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular ventas del día: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return BigDecimal.ZERO;
    }

    private List<Venta> ejecutarConsultaVentas(String sql) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Venta> ventas = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta de ventas: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return ventas;
    }

    private void cargarDetallesVenta(Venta venta, Connection conn) throws SQLException {
        String sql = """
            SELECT dv.*, p.codigo, p.nombre as producto_nombre, p.precio_venta
            FROM detalle_ventas dv
            INNER JOIN productos p ON dv.producto_id = p.id
            WHERE dv.venta_id = ?
            ORDER BY dv.id
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, venta.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setId(rs.getLong("id"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    detalle.setSubtotal(rs.getBigDecimal("subtotal"));
                    detalle.setObservaciones(rs.getString("observaciones"));

                    // Crear producto básico
                    Producto producto = new Producto();
                    producto.setId(rs.getLong("producto_id"));
                    producto.setCodigo(rs.getString("codigo"));
                    producto.setNombre(rs.getString("producto_nombre"));
                    
                    detalle.setProducto(producto);
                    venta.getDetalles().add(detalle);
                }
            }
        }
    }

    public Venta buscarPorNumero(String numeroVenta) {
        String sql = """
        SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
               u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
        FROM ventas v
        LEFT JOIN clientes c ON v.cliente_id = c.id
        INNER JOIN usuarios u ON v.usuario_id = u.id
        WHERE v.numero_venta = ?
        """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, numeroVenta);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Venta venta = mapResultSetToVenta(rs);
                cargarDetallesVenta(venta, conn);
                return venta;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al buscar venta por número: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Venta> obtenerVentasPorCliente(Long clienteId) {
        String sql = """
        SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
               u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
        FROM ventas v
        LEFT JOIN clientes c ON v.cliente_id = c.id
        INNER JOIN usuarios u ON v.usuario_id = u.id
        WHERE v.cliente_id = ?
        ORDER BY v.fecha_venta DESC
        """;

        List<Venta> ventas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, clienteId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }
            // load details for each venta
            for (Venta v : ventas) {
                cargarDetallesVenta(v, conn);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por cliente: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return ventas;
    }

    public List<Venta> obtenerVentasPorUsuario(Long usuarioId) {
        String sql = """
        SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
               u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
        FROM ventas v
        LEFT JOIN clientes c ON v.cliente_id = c.id
        INNER JOIN usuarios u ON v.usuario_id = u.id
        WHERE v.usuario_id = ?
        ORDER BY v.fecha_venta DESC
        """;

        List<Venta> ventas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, usuarioId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }
            for (Venta v : ventas) {
                cargarDetallesVenta(v, conn);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por usuario: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return ventas;
    }

    public List<Venta> obtenerVentasPorEstado(Estado estado) {
        String sql = """
        SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
               u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
        FROM ventas v
        LEFT JOIN clientes c ON v.cliente_id = c.id
        INNER JOIN usuarios u ON v.usuario_id = u.id
        WHERE v.estado = ?
        ORDER BY v.fecha_venta DESC
        """;

        List<Venta> ventas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, estado.name());
            rs = stmt.executeQuery();
            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }
            for (Venta v : ventas) {
                cargarDetallesVenta(v, conn);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por estado: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return ventas;
    }

    public boolean actualizarEstadoConObservaciones(Long ventaId, Estado nuevoEstado, String observaciones) {
        String sql = "UPDATE ventas SET estado = ?, observaciones = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nuevoEstado.name());
            if (observaciones != null) {
                stmt.setString(2, observaciones);
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setLong(3, ventaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado y observaciones de venta: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public int contarVentasPorEstado(Estado estado) {
        String sql = "SELECT COUNT(*) AS total FROM ventas WHERE estado = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, estado == null ? null : estado.name());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar ventas por estado: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return 0;
    }

    public List<Producto> obtenerProductosMasVendidos(int limite) {
        // Usa SQL Server syntax for TOP.
        String sql = "SELECT TOP " + Math.max(0, limite) + " p.id, p.codigo, p.nombre as producto_nombre, p.precio_venta, SUM(dv.cantidad) AS total_vendido " +
                "FROM detalle_ventas dv " +
                "INNER JOIN productos p ON dv.producto_id = p.id " +
                "INNER JOIN ventas v ON dv.venta_id = v.id " +
                "WHERE v.estado = 'COMPLETADA' " +
                "GROUP BY p.id, p.codigo, p.nombre, p.precio_venta " +
                "ORDER BY total_vendido DESC";

        List<Producto> productos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getLong("id"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("producto_nombre"));
                p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                // Note: total_vendido is not a field on Producto; if needed, extend Producto or return a DTO.
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return productos;
    }

    public List<Cliente> obtenerClientesConMasCompras(int limite) {
        // Returns top clients by number of completed purchases.
        String sql = "SELECT TOP " + Math.max(0, limite) + " c.id, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula, COUNT(v.id) AS compras, SUM(v.total) AS total_gastado " +
                "FROM ventas v " +
                "INNER JOIN clientes c ON v.cliente_id = c.id " +
                "WHERE v.estado = 'COMPLETADA' AND v.cliente_id IS NOT NULL " +
                "GROUP BY c.id, c.nombre, c.apellido, c.cedula " +
                "ORDER BY compras DESC, total_gastado DESC";

        List<Cliente> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNombre(rs.getString("cliente_nombre"));
                c.setApellido(rs.getString("cliente_apellido"));
                c.setCedula(rs.getString("cedula"));
                // If you need compras or total_gastado values, consider returning a DTO instead of Cliente.
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes con más compras: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
        return clientes;
    }



    private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getLong("id"));
        venta.setNumeroVenta(rs.getString("numero_venta"));
        venta.setSubtotal(rs.getBigDecimal("subtotal"));
        venta.setImpuesto(rs.getBigDecimal("impuesto"));
        venta.setDescuento(rs.getBigDecimal("descuento"));
        venta.setTotal(rs.getBigDecimal("total"));
        venta.setObservaciones(rs.getString("observaciones"));
        venta.setMetodoPago(rs.getString("metodo_pago"));

        // Mapear estado
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            venta.setEstado(Estado.valueOf(estadoStr));
        }

        // Mapear fecha
        Timestamp fechaVenta = rs.getTimestamp("fecha_venta");
        if (fechaVenta != null) {
            venta.setFechaVenta(fechaVenta.toLocalDateTime());
        }

        // Mapear cliente si existe
        Long clienteId = rs.getLong("cliente_id");
        if (clienteId != 0 && !rs.wasNull()) {
            Cliente cliente = new Cliente();
            cliente.setId(clienteId);
            cliente.setNombre(rs.getString("cliente_nombre"));
            cliente.setApellido(rs.getString("cliente_apellido"));
            cliente.setCedula(rs.getString("cedula"));
            venta.setCliente(cliente);
        }

        // Mapear usuario
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("usuario_id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setNombre(rs.getString("usuario_nombre"));
        usuario.setApellido(rs.getString("usuario_apellido"));
        venta.setUsuario(usuario);

        return venta;
    }

    public List<Venta> listarTodos() {
        String sql = """
        SELECT v.*, c.nombre as cliente_nombre, c.apellido as cliente_apellido, c.cedula,
               u.username, u.nombre as usuario_nombre, u.apellido as usuario_apellido
        FROM ventas v
        LEFT JOIN clientes c ON v.cliente_id = c.id
        INNER JOIN usuarios u ON v.usuario_id = u.id
        ORDER BY v.fecha_venta DESC
        """;

        return ejecutarConsultaVentas(sql);
    }
}
