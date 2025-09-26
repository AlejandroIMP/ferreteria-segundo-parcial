package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.Rol;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Rol
 */
public class RolDAO {
    private DatabaseManager dbManager;

    public RolDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean crear(Rol rol) {
        String sql = """
            INSERT INTO roles (nombre, descripcion, puede_vender, puede_gestionar_inventario,
                             puede_gestionar_usuarios, puede_generar_reportes, 
                             puede_gestionar_clientes, puede_gestionar_proveedores)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            stmt.setBoolean(3, rol.isPuedeVender());
            stmt.setBoolean(4, rol.isPuedeGestionarInventario());
            stmt.setBoolean(5, rol.isPuedeGestionarUsuarios());
            stmt.setBoolean(6, rol.isPuedeGenerarReportes());
            stmt.setBoolean(7, rol.isPuedeGestionarClientes());
            stmt.setBoolean(8, rol.isPuedeGestionarProveedores());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rol.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear rol: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public Rol buscarPorId(Long id) {
        String sql = "SELECT * FROM roles WHERE id = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRol(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar rol por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Rol buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM roles WHERE nombre = ? AND activo = 1";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRol(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar rol por nombre: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public List<Rol> listarTodos() {
        String sql = "SELECT * FROM roles ORDER BY nombre";
        List<Rol> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                roles.add(mapResultSetToRol(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar todos los roles: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return roles;
    }

    public List<Rol> listarActivos() {
        String sql = "SELECT * FROM roles WHERE activo = 1 ORDER BY nombre";
        List<Rol> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                roles.add(mapResultSetToRol(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar roles activos: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return roles;
    }

    public List<Rol> obtenerTodos() {
        return listarActivos();
    }

    public boolean actualizar(Rol rol) {
        String sql = """
            UPDATE roles 
            SET nombre = ?, descripcion = ?, puede_vender = ?, puede_gestionar_inventario = ?,
                puede_gestionar_usuarios = ?, puede_generar_reportes = ?, 
                puede_gestionar_clientes = ?, puede_gestionar_proveedores = ?, activo = ?
            WHERE id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            stmt.setBoolean(3, rol.isPuedeVender());
            stmt.setBoolean(4, rol.isPuedeGestionarInventario());
            stmt.setBoolean(5, rol.isPuedeGestionarUsuarios());
            stmt.setBoolean(6, rol.isPuedeGenerarReportes());
            stmt.setBoolean(7, rol.isPuedeGestionarClientes());
            stmt.setBoolean(8, rol.isPuedeGestionarProveedores());
            stmt.setBoolean(9, rol.isActivo());
            stmt.setLong(10, rol.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar rol: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean eliminar(Long id) {
        String sql = "DELETE FROM roles WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar rol: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public boolean desactivar(Long id) {
        String sql = "UPDATE roles SET activo = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al desactivar rol: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    private Rol mapResultSetToRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getLong("id"));
        rol.setNombre(rs.getString("nombre"));
        rol.setDescripcion(rs.getString("descripcion"));
        rol.setPuedeVender(rs.getBoolean("puede_vender"));
        rol.setPuedeGestionarInventario(rs.getBoolean("puede_gestionar_inventario"));
        rol.setPuedeGestionarUsuarios(rs.getBoolean("puede_gestionar_usuarios"));
        rol.setPuedeGenerarReportes(rs.getBoolean("puede_generar_reportes"));
        rol.setPuedeGestionarClientes(rs.getBoolean("puede_gestionar_clientes"));
        rol.setPuedeGestionarProveedores(rs.getBoolean("puede_gestionar_proveedores"));
        rol.setActivo(rs.getBoolean("activo"));
        return rol;
    }
}
