package com.ferreteriagd.sistemaFerreteria.dao;

import com.ferreteriagd.sistemaFerreteria.model.Usuario;
import com.ferreteriagd.sistemaFerreteria.model.Rol;
import com.ferreteriagd.sistemaFerreteria.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Usuario
 */
public class UsuarioDAO {
    private DatabaseManager dbManager;
    private RolDAO rolDAO;

    public UsuarioDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.rolDAO = new RolDAO();
    }

    public boolean crear(Usuario usuario) {
        String sql = """
            INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rol_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getApellido());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getTelefono());
            stmt.setLong(7, usuario.getRol().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getLong(1));
                        return true;
                    }
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public Usuario buscarPorId(Long id) {
        String sql = """
            SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion,
                   r.puede_vender, r.puede_gestionar_inventario, r.puede_gestionar_usuarios,
                   r.puede_generar_reportes, r.puede_gestionar_clientes, r.puede_gestionar_proveedores,
                   r.activo as rol_activo
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE u.id = ? AND u.activo = 1
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
                return mapResultSetToUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Usuario buscarPorUsername(String username) {
        String sql = """
            SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion,
                   r.puede_vender, r.puede_gestionar_inventario, r.puede_gestionar_usuarios,
                   r.puede_generar_reportes, r.puede_gestionar_clientes, r.puede_gestionar_proveedores,
                   r.activo as rol_activo
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE u.username = ? AND u.activo = 1
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = """
            SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion,
                   r.puede_vender, r.puede_gestionar_inventario, r.puede_gestionar_usuarios,
                   r.puede_generar_reportes, r.puede_gestionar_clientes, r.puede_gestionar_proveedores,
                   r.activo as rol_activo
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE u.email = ? AND u.activo = 1
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }
            return null;

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
            return null;
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }
    }

    public boolean actualizar(Usuario usuario) {
        String sql = """
            UPDATE usuarios 
            SET username = ?, password = ?, nombre = ?, apellido = ?, 
                email = ?, telefono = ?, rol_id = ?, ultimo_acceso = ?, activo = ?
            WHERE id = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getApellido());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getTelefono());
            stmt.setLong(7, usuario.getRol().getId());

            if (usuario.getUltimoAcceso() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(usuario.getUltimoAcceso()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }

            stmt.setBoolean(9, usuario.isActivo());
            stmt.setLong(10, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    public List<Usuario> listarTodos() {
        String sql = """
            SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion,
                   r.puede_vender, r.puede_gestionar_inventario, r.puede_gestionar_usuarios,
                   r.puede_generar_reportes, r.puede_gestionar_clientes, r.puede_gestionar_proveedores,
                   r.activo as rol_activo
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.id
            ORDER BY u.nombre, u.apellido
            """;

        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return usuarios;
    }

    public List<Usuario> listarActivos() {
        String sql = """
            SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion,
                   r.puede_vender, r.puede_gestionar_inventario, r.puede_gestionar_usuarios,
                   r.puede_generar_reportes, r.puede_gestionar_clientes, r.puede_gestionar_proveedores,
                   r.activo as rol_activo
            FROM usuarios u
            INNER JOIN roles r ON u.rol_id = r.id
            WHERE u.activo = 1
            ORDER BY u.nombre, u.apellido
            """;

        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios activos: " + e.getMessage());
        } finally {
            dbManager.closeResources(conn, stmt, rs);
        }

        return usuarios;
    }

    public boolean eliminar(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        } finally {
            dbManager.closeResources(conn, stmt);
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        // Datos del usuario
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setActivo(rs.getBoolean("activo"));

        // Fechas
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        // Crear el rol
        Rol rol = new Rol();
        rol.setId(rs.getLong("rol_id"));
        rol.setNombre(rs.getString("rol_nombre"));
        rol.setDescripcion(rs.getString("rol_descripcion"));
        rol.setPuedeVender(rs.getBoolean("puede_vender"));
        rol.setPuedeGestionarInventario(rs.getBoolean("puede_gestionar_inventario"));
        rol.setPuedeGestionarUsuarios(rs.getBoolean("puede_gestionar_usuarios"));
        rol.setPuedeGenerarReportes(rs.getBoolean("puede_generar_reportes"));
        rol.setPuedeGestionarClientes(rs.getBoolean("puede_gestionar_clientes"));
        rol.setPuedeGestionarProveedores(rs.getBoolean("puede_gestionar_proveedores"));
        rol.setActivo(rs.getBoolean("rol_activo"));

        usuario.setRol(rol);

        return usuario;
    }
}
