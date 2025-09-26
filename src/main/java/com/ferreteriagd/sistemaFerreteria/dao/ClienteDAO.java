package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.Cliente;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Cliente
 */
public class ClienteDAO {
    private DatabaseManager dbManager;

    public ClienteDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean crear(Cliente cliente) {
        String sql = """
            INSERT INTO clientes (nombre, apellido, cedula, telefono, email, direccion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getCedula());
            stmt.setString(4, cliente.getTelefono());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getDireccion());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM clientes WHERE id = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Cliente buscarPorCedula(String cedula) {
        String sql = "SELECT * FROM clientes WHERE cedula = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cedula);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCliente(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por cédula: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        String sql = """
            SELECT * FROM clientes 
            WHERE (nombre LIKE ? OR apellido LIKE ? OR CONCAT(nombre, ' ', apellido) LIKE ?)
            AND activo = 1
            ORDER BY nombre, apellido
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            String searchTerm = "%" + nombre + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar clientes por nombre: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return clientes;
    }

    public List<Cliente> obtenerTodos() {
        String sql = "SELECT * FROM clientes WHERE activo = 1 ORDER BY nombre, apellido";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return clientes;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = """
            UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ?
            WHERE id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getDireccion());
            stmt.setLong(6, cliente.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "UPDATE clientes SET activo = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public int contarClientesActivos() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al contar clientes activos: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return 0;
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setApellido(rs.getString("apellido"));
        cliente.setCedula(rs.getString("cedula"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setActivo(rs.getBoolean("activo"));

        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            cliente.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        return cliente;
    }
}
