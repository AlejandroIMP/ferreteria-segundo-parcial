package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.Cliente;
import com.ferreteriagd.sistemaFerreteria.dao.ClienteDAO;

import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    // Crear nuevo cliente
    public boolean crearCliente(String nombre, String apellido, String cedula, 
                               String telefono, String email, String direccion) {
        try {
            // Validar que la cédula no exista
            if (buscarPorCedula(cedula) != null) {
                return false; // Cédula duplicada
            }

            // Validar email único si se proporciona
            if (email != null && !email.isEmpty() && buscarPorEmail(email) != null) {
                return false; // Email duplicado
            }

            Cliente cliente = new Cliente(nombre, apellido, cedula, telefono, email, direccion);
            return clienteDAO.crear(cliente);
        } catch (Exception e) {
            System.err.println("Error en ClienteController.crearCliente: " + e.getMessage());
            return false;
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Long id) {
        return clienteDAO.buscarPorId(id);
    }

    // Buscar cliente por cédula
    public Cliente buscarPorCedula(String cedula) {
        return clienteDAO.buscarPorCedula(cedula);
    }

    // Buscar cliente por email
    public Cliente buscarPorEmail(String email) {
        return clienteDAO.obtenerTodos().stream()
                .filter(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Buscar clientes por nombre (búsqueda parcial)
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteDAO.buscarPorNombre(nombre);
    }

    // Buscar clientes por teléfono
    public List<Cliente> buscarPorTelefono(String telefono) {
        return clienteDAO.obtenerTodos().stream()
                .filter(c -> c.getTelefono() != null && c.getTelefono().contains(telefono))
                .toList();
    }

    // Obtener todos los clientes activos
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteDAO.obtenerTodos();
    }

    // Actualizar cliente
    public boolean actualizarCliente(Long id, String nombre, String apellido, 
                                   String telefono, String email, String direccion) {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente != null) {
            // Validar email único si se cambia
            if (email != null && !email.equals(cliente.getEmail())) {
                Cliente clienteConEmail = buscarPorEmail(email);
                if (clienteConEmail != null && !clienteConEmail.getId().equals(id)) {
                    return false; // Email ya existe
                }
            }

            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setDireccion(direccion);
            return clienteDAO.actualizar(cliente);
        }
        return false;
    }

    // Eliminar cliente (soft delete)
    public boolean eliminarCliente(Long id) {
        return clienteDAO.eliminar(id);
    }

    // Reactivar cliente
    public boolean reactivarCliente(Long id) {
        // Esta funcionalidad requeriría un método específico en el DAO
        // Por ahora retorna false
        return false;
    }

    // Validar datos del cliente
    public boolean validarCedula(String cedula) {
        return cedula != null && cedula.length() >= 8 && cedula.matches("\\d+");
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

    // Obtener clientes inactivos
    public List<Cliente> obtenerClientesInactivos() {
        // Esta funcionalidad requeriría un método específico en el DAO
        return List.of();
    }

    // Buscar clientes con información incompleta
    public List<Cliente> obtenerClientesConDatosIncompletos() {
        return clienteDAO.obtenerTodos().stream()
                .filter(c -> c.getEmail() == null || c.getEmail().isEmpty() ||
                           c.getDireccion() == null || c.getDireccion().isEmpty())
                .toList();
    }

    // Estadísticas
    public int contarClientesActivos() {
        return clienteDAO.contarClientesActivos();
    }

    public int contarClientesInactivos() {
        // Esta funcionalidad requeriría un método específico en el DAO
        return 0;
    }

    public int contarClientesTotales() {
        return clienteDAO.obtenerTodos().size();
    }

    // Obtener clientes ordenados alfabéticamente
    public List<Cliente> obtenerClientesOrdenados() {
        return clienteDAO.obtenerTodos().stream()
                .sorted((c1, c2) -> c1.getNombreCompleto().compareToIgnoreCase(c2.getNombreCompleto()))
                .toList();
    }

    // Verificar si un cliente tiene compras (para validar eliminación)
    public boolean clienteTieneCompras(Long clienteId) {
        // Esta lógica se implementaría consultando VentaDAO
        return false;
    }
}
