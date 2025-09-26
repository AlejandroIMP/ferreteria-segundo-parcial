package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Proveedor
 */
public class ProveedorDAO {
    private DatabaseManager dbManager;

    public ProveedorDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean crear(Proveedor proveedor) {
        String sql = """
            INSERT INTO proveedores (nombre, razon_social, ruc, telefono, email, direccion, contacto, telefono_contacto)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getRazonSocial());
            stmt.setString(3, proveedor.getRuc());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEmail());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setString(7, proveedor.getContacto());
            stmt.setString(8, proveedor.getTelefonoContacto());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proveedor.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear proveedor: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public Proveedor buscarPorId(Long id) {
        String sql = "SELECT * FROM proveedores WHERE id = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProveedor(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Proveedor buscarPorRuc(String ruc) {
        String sql = "SELECT * FROM proveedores WHERE ruc = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, ruc);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProveedor(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor por RUC: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Proveedor> obtenerTodos() {
        String sql = "SELECT * FROM proveedores WHERE activo = 1 ORDER BY nombre";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Proveedor> proveedores = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return proveedores;
    }

    public boolean actualizar(Proveedor proveedor) {
        String sql = """
            UPDATE proveedores 
            SET nombre = ?, razon_social = ?, telefono = ?, email = ?, direccion = ?, 
                contacto = ?, telefono_contacto = ?
            WHERE id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getRazonSocial());
            stmt.setString(3, proveedor.getTelefono());
            stmt.setString(4, proveedor.getEmail());
            stmt.setString(5, proveedor.getDireccion());
            stmt.setString(6, proveedor.getContacto());
            stmt.setString(7, proveedor.getTelefonoContacto());
            stmt.setLong(8, proveedor.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "UPDATE proveedores SET activo = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    private Proveedor mapResultSetToProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(rs.getLong("id"));
        proveedor.setNombre(rs.getString("nombre"));
        proveedor.setRazonSocial(rs.getString("razon_social"));
        proveedor.setRuc(rs.getString("ruc"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setEmail(rs.getString("email"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setContacto(rs.getString("contacto"));
        proveedor.setTelefonoContacto(rs.getString("telefono_contacto"));
        proveedor.setActivo(rs.getBoolean("activo"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            proveedor.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        return proveedor;
    }
}
