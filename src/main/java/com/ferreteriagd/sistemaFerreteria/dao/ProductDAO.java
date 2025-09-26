package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.Producto;
import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Producto
 */
public class ProductDAO {
    private DatabaseManager dbManager;
    private ProveedorDAO proveedorDAO;

    public ProductDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.proveedorDAO = new ProveedorDAO();
    }

    public boolean crear(Producto producto) {
        String sql = """
            INSERT INTO productos (codigo, nombre, descripcion, categoria, marca, modelo, 
                                 precio_compra, precio_venta, stock, stock_minimo, unidad_medida, proveedor_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getCategoria());
            stmt.setString(5, producto.getMarca());
            stmt.setString(6, producto.getModelo());
            stmt.setBigDecimal(7, producto.getPrecioCompra());
            stmt.setBigDecimal(8, producto.getPrecioVenta());
            stmt.setInt(9, producto.getStock());
            stmt.setInt(10, producto.getStockMinimo());
            stmt.setString(11, producto.getUnidadMedida());

            if (producto.getProveedor() != null) {
                stmt.setLong(12, producto.getProveedor().getId());
            } else {
                stmt.setNull(12, Types.BIGINT);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public Producto buscarPorId(Long id) {
        String sql = """
            SELECT p.*, pr.nombre as proveedor_nombre, pr.razon_social, pr.ruc,
                   pr.telefono as proveedor_telefono, pr.email as proveedor_email
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            WHERE p.id = ? AND p.activo = 1
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
                return mapResultSetToProducto(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Producto buscarPorCodigo(String codigo) {
        String sql = """
            SELECT p.*, pr.nombre as proveedor_nombre, pr.razon_social, pr.ruc,
                   pr.telefono as proveedor_telefono, pr.email as proveedor_email
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            WHERE p.codigo = ? AND p.activo = 1
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigo);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProducto(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por código: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Producto> buscarPorNombre(String nombre) {
        String sql = """
            SELECT p.*, pr.nombre as proveedor_nombre, pr.razon_social, pr.ruc,
                   pr.telefono as proveedor_telefono, pr.email as proveedor_email
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            WHERE p.nombre LIKE ? AND p.activo = 1
            ORDER BY p.nombre
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Producto> productos = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + nombre + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return productos;
    }

    public List<Producto> obtenerTodos() {
        String sql = """
            SELECT p.*, pr.nombre as proveedor_nombre, pr.razon_social, pr.ruc,
                   pr.telefono as proveedor_telefono, pr.email as proveedor_email
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            WHERE p.activo = 1
            ORDER BY p.nombre
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Producto> productos = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return productos;
    }

    public boolean actualizar(Producto producto) {
        String sql = """
            UPDATE productos 
            SET nombre = ?, descripcion = ?, categoria = ?, marca = ?, modelo = ?,
                precio_compra = ?, precio_venta = ?, stock_minimo = ?, unidad_medida = ?,
                proveedor_id = ?, fecha_actualizacion = GETDATE()
            WHERE id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getCategoria());
            stmt.setString(4, producto.getMarca());
            stmt.setString(5, producto.getModelo());
            stmt.setBigDecimal(6, producto.getPrecioCompra());
            stmt.setBigDecimal(7, producto.getPrecioVenta());
            stmt.setInt(8, producto.getStockMinimo());
            stmt.setString(9, producto.getUnidadMedida());

            if (producto.getProveedor() != null) {
                stmt.setLong(10, producto.getProveedor().getId());
            } else {
                stmt.setNull(10, Types.BIGINT);
            }

            stmt.setLong(11, producto.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean actualizarStock(Long productoId, int nuevoStock) {
        String sql = "UPDATE productos SET stock = ?, fecha_actualizacion = GETDATE() WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, nuevoStock);
            stmt.setLong(2, productoId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "UPDATE productos SET activo = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public List<Producto> obtenerProductosConStockBajo() {
        String sql = """
            SELECT p.*, pr.nombre as proveedor_nombre, pr.razon_social, pr.ruc,
                   pr.telefono as proveedor_telefono, pr.email as proveedor_email
            FROM productos p
            LEFT JOIN proveedores pr ON p.proveedor_id = pr.id
            WHERE p.stock <= p.stock_minimo AND p.activo = 1
            ORDER BY p.nombre
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Producto> productos = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return productos;
    }

    public BigDecimal calcularValorInventario() {
        String sql = "SELECT SUM(precio_compra * stock) as valor_total FROM productos WHERE activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal valor = rs.getBigDecimal("valor_total");
                return valor != null ? valor : BigDecimal.ZERO;
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular valor del inventario: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return BigDecimal.ZERO;
    }

    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong("id"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setMarca(rs.getString("marca"));
        producto.setModelo(rs.getString("modelo"));
        producto.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        producto.setStock(rs.getInt("stock"));
        producto.setStockMinimo(rs.getInt("stock_minimo"));
        producto.setUnidadMedida(rs.getString("unidad_medida"));
        producto.setActivo(rs.getBoolean("activo"));

        // Mapear fechas
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            producto.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            producto.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        // Mapear proveedor si existe
        Long proveedorId = rs.getLong("proveedor_id");
        if (proveedorId != 0 && !rs.wasNull()) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(proveedorId);
            proveedor.setNombre(rs.getString("proveedor_nombre"));
            proveedor.setRazonSocial(rs.getString("razon_social"));
            proveedor.setRuc(rs.getString("ruc"));
            proveedor.setTelefono(rs.getString("proveedor_telefono"));
            proveedor.setEmail(rs.getString("proveedor_email"));
            producto.setProveedor(proveedor);
        }

        return producto;
    }
}
