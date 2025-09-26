package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.Rol;
import com.ferreteriagd.sistemaFerreteria.dao.RolDAO;

import java.util.List;

public class RolController {
    private final RolDAO rolDAO;

    public RolController() {
        this.rolDAO = new RolDAO();
    }

    // Crear nuevo rol
    public boolean crearRol(String nombre, String descripcion, boolean puedeVender,
                            boolean puedeGestionarInventario, boolean puedeGestionarUsuarios,
                            boolean puedeGenerarReportes, boolean puedeGestionarClientes,
                            boolean puedeGestionarProveedores) {
        try {
            // Validar que el nombre no exista
            if (buscarPorNombre(nombre) != null) {
                return false; // Nombre duplicado
            }

            Rol rol = new Rol(nombre.toUpperCase(), descripcion, puedeVender,
                    puedeGestionarInventario, puedeGestionarUsuarios,
                    puedeGenerarReportes, puedeGestionarClientes,
                    puedeGestionarProveedores);
            return rolDAO.crear(rol);
        } catch (Exception e) {
            System.err.println("Error en RolController.crearRol: " + e.getMessage());
            return false;
        }
    }

    // Buscar rol por ID
    public Rol buscarPorId(Long id) {
        return rolDAO.buscarPorId(id);
    }

    // Buscar rol por nombre
    public Rol buscarPorNombre(String nombre) {
        return rolDAO.buscarPorNombre(nombre);
    }

    // Obtener todos los roles
    public List<Rol> listarTodos() {
        return rolDAO.listarTodos();
    }

    // Obtener roles activos
    public List<Rol> listarActivos() {
        return rolDAO.listarActivos();
    }

    // Actualizar rol
    public boolean actualizar(Rol rol) {
        try {
            return rolDAO.actualizar(rol);
        } catch (Exception e) {
            System.err.println("Error en RolController.actualizar: " + e.getMessage());
            return false;
        }
    }

    // Desactivar rol
    public boolean desactivar(Long id) {
        try {
            Rol rol = buscarPorId(id);
            if (rol != null) {
                rol.setActivo(false);
                return rolDAO.actualizar(rol);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en RolController.desactivar: " + e.getMessage());
            return false;
        }
    }

    // Activar rol
    public boolean activar(Long id) {
        try {
            Rol rol = buscarPorId(id);
            if (rol != null) {
                rol.setActivo(true);
                return rolDAO.actualizar(rol);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en RolController.activar: " + e.getMessage());
            return false;
        }
    }

    // Inicializar roles predefinidos del sistema
    public void inicializarRolesSistema() {
        try {
            // Verificar si ya existen roles
            List<Rol> roles = listarTodos();
            if (!roles.isEmpty()) {
                return; // Ya hay roles creados
            }

            // Crear roles predefinidos
            System.out.println("Inicializando roles del sistema...");

            // Rol Administrador
            if (buscarPorNombre("ADMINISTRADOR") == null) {
                Rol admin = Rol.createAdministrador();
                rolDAO.crear(admin);
                System.out.println("Rol ADMINISTRADOR creado");
            }

            // Rol Vendedor
            if (buscarPorNombre("VENDEDOR") == null) {
                Rol vendedor = Rol.createVendedor();
                rolDAO.crear(vendedor);
                System.out.println("Rol VENDEDOR creado");
            }

            // Rol Inventario
            if (buscarPorNombre("INVENTARIO") == null) {
                Rol inventario = Rol.createInventario();
                rolDAO.crear(inventario);
                System.out.println("Rol INVENTARIO creado");
            }

            System.out.println("Roles del sistema inicializados correctamente");

        } catch (Exception e) {
            System.err.println("Error al inicializar roles del sistema: " + e.getMessage());
        }
    }

    // Verificar si un rol puede ser eliminado (no tiene usuarios asignados)
    public boolean puedeEliminarRol(Long rolId) {
        try {
            // Aquí deberías verificar si hay usuarios con este rol
            // Por ahora retornamos true, pero en una implementación completa
            // verificarías la tabla usuarios
            return true;
        } catch (Exception e) {
            System.err.println("Error en RolController.puedeEliminarRol: " + e.getMessage());
            return false;
        }
    }

    // Obtener todos los roles activos
    public List<Rol> obtenerTodosLosRoles() {
        return rolDAO.obtenerTodos();
    }

    // Actualizar rol
    public boolean actualizarRol(Long id, String descripcion, boolean puedeVender,
                                boolean puedeGestionarInventario, boolean puedeGestionarUsuarios,
                                boolean puedeGenerarReportes, boolean puedeGestionarClientes,
                                boolean puedeGestionarProveedores) {
        Rol rol = rolDAO.buscarPorId(id);
        if (rol != null) {
            rol.setDescripcion(descripcion);
            rol.setPuedeVender(puedeVender);
            rol.setPuedeGestionarInventario(puedeGestionarInventario);
            rol.setPuedeGestionarUsuarios(puedeGestionarUsuarios);
            rol.setPuedeGenerarReportes(puedeGenerarReportes);
            rol.setPuedeGestionarClientes(puedeGestionarClientes);
            rol.setPuedeGestionarProveedores(puedeGestionarProveedores);
            return rolDAO.actualizar(rol);
        }
        return false;
    }

    // Eliminar rol (soft delete)
    public boolean eliminarRol(Long id) {
        Rol rol = rolDAO.buscarPorId(id);
        if (rol != null && !esRolDefault(rol.getNombre())) {
            // Verificar si hay usuarios con este rol
            if (!rolTieneUsuarios(id)) {
                return rolDAO.eliminar(id);
            }
        }
        return false;
    }

    // Obtener roles con permisos específicos
    public List<Rol> obtenerRolesConPermiso(String tipoPermiso) {
        return rolDAO.obtenerTodos().stream()
                .filter(r -> tienePermiso(r, tipoPermiso))
                .toList();
    }

    // Verificar si un rol tiene un permiso específico
    private boolean tienePermiso(Rol rol, String tipoPermiso) {
        return switch (tipoPermiso.toLowerCase()) {
            case "vender" -> rol.isPuedeVender();
            case "inventario" -> rol.isPuedeGestionarInventario();
            case "usuarios" -> rol.isPuedeGestionarUsuarios();
            case "reportes" -> rol.isPuedeGenerarReportes();
            case "clientes" -> rol.isPuedeGestionarClientes();
            case "proveedores" -> rol.isPuedeGestionarProveedores();
            default -> false;
        };
    }

    // Verificar si es un rol por defecto (no eliminable)
    private boolean esRolDefault(String nombreRol) {
        return nombreRol.equals("ADMINISTRADOR") ||
               nombreRol.equals("VENDEDOR") ||
               nombreRol.equals("INVENTARIO");
    }

    // Verificar si el rol tiene usuarios asignados
    private boolean rolTieneUsuarios(Long rolId) {
        // Esta lógica se implementaría consultando el UsuarioDAO
        return false;
    }

    // Obtener descripción completa de permisos
    public String obtenerDescripcionPermisos(Long id) {
        Rol rol = rolDAO.buscarPorId(id);
        if (rol == null) {
            return null;
        }

        StringBuilder permisos = new StringBuilder();
        permisos.append("Permisos del rol ").append(rol.getNombre()).append(":\n");
        permisos.append("- Realizar ventas: ").append(rol.isPuedeVender() ? "SÍ" : "NO").append("\n");
        permisos.append("- Gestionar inventario: ").append(rol.isPuedeGestionarInventario() ? "SÍ" : "NO").append("\n");
        permisos.append("- Gestionar usuarios: ").append(rol.isPuedeGestionarUsuarios() ? "SÍ" : "NO").append("\n");
        permisos.append("- Generar reportes: ").append(rol.isPuedeGenerarReportes() ? "SÍ" : "NO").append("\n");
        permisos.append("- Gestionar clientes: ").append(rol.isPuedeGestionarClientes() ? "SÍ" : "NO").append("\n");
        permisos.append("- Gestionar proveedores: ").append(rol.isPuedeGestionarProveedores() ? "SÍ" : "NO").append("\n");

        return permisos.toString();
    }

    // Estadísticas
    public int contarRolesActivos() {
        return rolDAO.obtenerTodos().size();
    }

    // Validar permisos para crear rol personalizado
    public boolean puedeCrearRolPersonalizado(Rol rolUsuario) {
        return rolUsuario != null && rolUsuario.isPuedeGestionarUsuarios();
    }

    public boolean actualizarRol(Long id, String nombre, String descripcion, boolean activo) {
        Rol rol = rolDAO.buscarPorId(id);
        if (rol != null) {
            // If nombre is provided and different, update it
            if (nombre != null && !nombre.isEmpty() && !nombre.equals(rol.getNombre())) {
                rol.setNombre(nombre.toUpperCase());
            }
            // Update description
            rol.setDescripcion(descripcion);
            // Keep existing permissions
            return rolDAO.actualizar(rol);
        }
        return false;
    }

    public boolean tieneUsuariosAsignados(Long id) {
        return rolTieneUsuarios(id);
    }
}
