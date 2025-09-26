package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import com.ferreteriagd.sistemaFerreteria.dao.ProveedorDAO;

import java.util.List;

public class ProveedorController {
    private ProveedorDAO proveedorDAO;

    public ProveedorController() {
        this.proveedorDAO = new ProveedorDAO();
    }

    // Crear nuevo proveedor
    public boolean crearProveedor(String nombre, String razonSocial, String ruc, 
                                 String telefono, String email, String direccion) {
        try {
            // Validar que el RUC no exista
            if (buscarPorRuc(ruc) != null) {
                return false; // RUC duplicado
            }

            // Validar email único si se proporciona
            if (email != null && !email.isEmpty() && buscarPorEmail(email) != null) {
                return false; // Email duplicado
            }

            Proveedor proveedor = new Proveedor(nombre, razonSocial, ruc, telefono, email, direccion);
            return proveedorDAO.crear(proveedor);
        } catch (Exception e) {
            System.err.println("Error en ProveedorController.crearProveedor: " + e.getMessage());
            return false;
        }
    }

    // Buscar proveedor por ID
    public Proveedor buscarPorId(Long id) {
        return proveedorDAO.buscarPorId(id);
    }

    // Buscar proveedor por RUC
    public Proveedor buscarPorRuc(String ruc) {
        return proveedorDAO.buscarPorRuc(ruc);
    }

    // Buscar proveedor por email
    public Proveedor buscarPorEmail(String email) {
        return proveedorDAO.obtenerTodos().stream()
                .filter(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Buscar proveedores por nombre (búsqueda parcial)
    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorDAO.obtenerTodos().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()) ||
                           p.getRazonSocial().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    // Obtener todos los proveedores activos
    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorDAO.obtenerTodos();
    }

    // Actualizar proveedor
    public boolean actualizarProveedor(Long id, String nombre, String razonSocial, 
                                     String telefono, String email, String direccion,
                                     String contacto, String telefonoContacto) {
        Proveedor proveedor = proveedorDAO.buscarPorId(id);
        if (proveedor != null) {
            // Validar email único si se cambia
            if (email != null && !email.equals(proveedor.getEmail())) {
                Proveedor proveedorConEmail = buscarPorEmail(email);
                if (proveedorConEmail != null && !proveedorConEmail.getId().equals(id)) {
                    return false; // Email ya existe
                }
            }

            proveedor.setNombre(nombre);
            proveedor.setRazonSocial(razonSocial);
            proveedor.setTelefono(telefono);
            proveedor.setEmail(email);
            proveedor.setDireccion(direccion);
            proveedor.setContacto(contacto);
            proveedor.setTelefonoContacto(telefonoContacto);
            return proveedorDAO.actualizar(proveedor);
        }
        return false;
    }

    // Eliminar proveedor (soft delete)
    public boolean eliminarProveedor(Long id) {
        // Verificar si tiene productos asociados
        if (proveedorTieneProductos(id)) {
            return false; // No se puede eliminar si tiene productos
        }
        return proveedorDAO.eliminar(id);
    }

    // Reactivar proveedor
    public boolean reactivarProveedor(Long id) {
        // Esta funcionalidad requeriría un método específico en el DAO
        return false;
    }

    // Validaciones
    public boolean validarRuc(String ruc) {
        return ruc != null && ruc.length() >= 8 && ruc.matches("\\d+");
    }

    public boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true; // Email es opcional
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean validarTelefono(String telefono) {
        return telefono != null && telefono.length() >= 7 && telefono.matches("[\\d\\-\\+\\s\\(\\)]+");
    }

    // Obtener proveedores inactivos
    public List<Proveedor> obtenerProveedoresInactivos() {
        // Esta funcionalidad requeriría un método específico en el DAO
        return List.of();
    }

    // Buscar proveedores con información incompleta
    public List<Proveedor> obtenerProveedoresConDatosIncompletos() {
        return proveedorDAO.obtenerTodos().stream()
                .filter(p -> p.getEmail() == null || p.getEmail().isEmpty() ||
                           p.getDireccion() == null || p.getDireccion().isEmpty() ||
                           p.getContacto() == null || p.getContacto().isEmpty())
                .toList();
    }

    // Estadísticas
    public int contarProveedoresActivos() {
        return proveedorDAO.obtenerTodos().size();
    }

    public int contarProveedoresInactivos() {
        // Esta funcionalidad requeriría un método específico en el DAO
        return 0;
    }

    public int contarProveedoresTotales() {
        return proveedorDAO.obtenerTodos().size();
    }

    // Obtener proveedores ordenados alfabéticamente
    public List<Proveedor> obtenerProveedoresOrdenados() {
        return proveedorDAO.obtenerTodos().stream()
                .sorted((p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()))
                .toList();
    }

    // Verificar si un proveedor tiene productos (para validar eliminación)
    private boolean proveedorTieneProductos(Long proveedorId) {
        // Esta lógica se implementaría consultando el ProductDAO
        return false;
    }

    // Obtener información de contacto completa
    public String obtenerInfoContacto(Long id) {
        Proveedor proveedor = proveedorDAO.buscarPorId(id);
        if (proveedor != null) {
            StringBuilder info = new StringBuilder();
            info.append("Empresa: ").append(proveedor.getNombre()).append("\n");
            if (proveedor.getContacto() != null) {
                info.append("Contacto: ").append(proveedor.getContacto()).append("\n");
            }
            info.append("Teléfono: ").append(proveedor.getTelefono()).append("\n");
            if (proveedor.getTelefonoContacto() != null) {
                info.append("Teléfono Contacto: ").append(proveedor.getTelefonoContacto()).append("\n");
            }
            if (proveedor.getEmail() != null) {
                info.append("Email: ").append(proveedor.getEmail()).append("\n");
            }
            if (proveedor.getDireccion() != null) {
                info.append("Dirección: ").append(proveedor.getDireccion());
            }
            return info.toString();
        }
        return null;
    }
}
