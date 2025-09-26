package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.Usuario;
import com.ferreteriagd.sistemaFerreteria.model.Rol;
import com.ferreteriagd.sistemaFerreteria.dao.UsuarioDAO;
import com.ferreteriagd.sistemaFerreteria.dao.RolDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

public class UsuarioController {
    private UsuarioDAO usuarioDAO;
    private RolDAO rolDAO;
    private Usuario usuarioActual;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.rolDAO = new RolDAO();
        this.usuarioActual = null;
    }

    // Métodos de Autenticación
    public boolean iniciarSesion(String username, String password) {
        try {
            Usuario usuario = buscarPorUsername(username);
            if (usuario != null && usuario.isActivo()) {
                String passwordEncriptada = encriptarPassword(password);
                if (usuario.getPassword().equals(passwordEncriptada)) {
                    this.usuarioActual = usuario;
                    usuario.actualizarUltimoAcceso();
                    usuarioDAO.actualizar(usuario);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en iniciarSesion: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarUsuario(String username, String password, String confirmarPassword,
                                  String nombre, String apellido, String email, String telefono, Long rolId) {
        try {
            // Validaciones
            if (!password.equals(confirmarPassword)) {
                return false; // Contraseñas no coinciden
            }

            if (username == null || username.trim().isEmpty() || password.length() < 6) {
                return false; // Datos inválidos
            }

            return crearUsuario(username, password, nombre, apellido, email, telefono, rolId);
        } catch (Exception e) {
            System.err.println("Error en registrarUsuario: " + e.getMessage());
            return false;
        }
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean isUsuarioLogueado() {
        return usuarioActual != null;
    }

    // Métodos de autorización basados en roles
    public boolean puedeVender() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeVender();
    }

    public boolean puedeGestionarInventario() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeGestionarInventario();
    }

    public boolean puedeGestionarUsuarios() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeGestionarUsuarios();
    }

    public boolean puedeGenerarReportes() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeGenerarReportes();
    }

    public boolean puedeGestionarClientes() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeGestionarClientes();
    }

    public boolean puedeGestionarProveedores() {
        return usuarioActual != null && usuarioActual.getRol().isPuedeGestionarProveedores();
    }

    // Crear nuevo usuario
    public boolean crearUsuario(String username, String password, String nombre, String apellido,
                               String email, String telefono, Long rolId) {
        try {
            // Validar que el username no exista
            if (buscarPorUsername(username) != null) {
                return false; // Username duplicado
            }

            // Validar email único si se proporciona
            if (email != null && !email.isEmpty() && buscarPorEmail(email) != null) {
                return false; // Email duplicado
            }

            // Buscar el rol
            Rol rol = rolDAO.buscarPorId(rolId);
            if (rol == null) {
                return false; // Rol no existe
            }

            // Encriptar contraseña
            String passwordEncriptada = encriptarPassword(password);
            
            Usuario usuario = new Usuario(username, passwordEncriptada, nombre, apellido, email, rol);
            usuario.setTelefono(telefono);
            return usuarioDAO.crear(usuario);
        } catch (Exception e) {
            System.err.println("Error en UsuarioController.crearUsuario: " + e.getMessage());
            return false;
        }
    }

    // Buscar usuario por username
    public Usuario buscarPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }

    // Buscar usuario por email
    public Usuario buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    // Actualizar usuario
    public boolean actualizar(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    // Cambiar contraseña
    public boolean cambiarPassword(String passwordActual, String nuevaPassword) {
        if (usuarioActual == null) return false;

        String passwordActualEncriptada = encriptarPassword(passwordActual);
        if (!usuarioActual.getPassword().equals(passwordActualEncriptada)) {
            return false; // Contraseña actual incorrecta
        }

        String nuevaPasswordEncriptada = encriptarPassword(nuevaPassword);
        usuarioActual.setPassword(nuevaPasswordEncriptada);
        return usuarioDAO.actualizar(usuarioActual);
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    // Desactivar usuario
    public boolean desactivar(Long id) {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(false);
            return usuarioDAO.actualizar(usuario);
        }
        return false;
    }

    // Activar usuario
    public boolean activar(Long id) {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(true);
            return usuarioDAO.actualizar(usuario);
        }
        return false;
    }

    // Encriptar contraseña usando SHA-256
    private String encriptarPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error al encriptar contraseña: " + e.getMessage());
            return password; // En caso de error, retorna la contraseña sin encriptar
        }
    }

    // Obtener información del dashboard para el usuario actual
    public String getInfoDashboard() {
        if (usuarioActual == null) return "Usuario no autenticado";

        StringBuilder info = new StringBuilder();
        info.append("Usuario: ").append(usuarioActual.getNombreCompleto()).append("\n");
        info.append("Rol: ").append(usuarioActual.getRol().getNombre()).append("\n");
        info.append("Último acceso: ").append(usuarioActual.getUltimoAcceso()).append("\n");

        return info.toString();
    }

    // Métodos adicionales necesarios para compatibilidad con otras clases
    public boolean autenticar(String username, String password) {
        return iniciarSesion(username, password);
    }

    public int contarUsuariosActivos() {
        return usuarioDAO.listarActivos().size();
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return listarTodos();
    }

    public List<Usuario> obtenerUsuariosPorRol(String nombreRol) {
        List<Usuario> todosLosUsuarios = listarTodos();
        return todosLosUsuarios.stream()
                .filter(u -> u.getRol().getNombre().equals(nombreRol))
                .toList();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    public boolean actualizarUsuario(Long id, String nombre, String apellido, String email, String telefono, Long rolId) {
        try {
            Usuario usuario = buscarPorId(id);
            if (usuario != null) {
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuario.setEmail(email);
                usuario.setTelefono(telefono);

                if (rolId != null) {
                    Rol rol = rolDAO.buscarPorId(rolId);
                    if (rol != null) {
                        usuario.setRol(rol);
                    }
                }

                return actualizar(usuario);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en actualizarUsuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(Long id) {
        try {
            return desactivar(id);
        } catch (Exception e) {
            System.err.println("Error en eliminarUsuario: " + e.getMessage());
            return false;
        }
    }

    public boolean restablecerPassword(Long id, String nuevaPassword) {
        try {
            Usuario usuario = buscarPorId(id);
            if (usuario != null) {
                String passwordEncriptada = encriptarPassword(nuevaPassword);
                usuario.setPassword(passwordEncriptada);
                return actualizar(usuario);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en restablecerPassword: " + e.getMessage());
            return false;
        }
    }
}
