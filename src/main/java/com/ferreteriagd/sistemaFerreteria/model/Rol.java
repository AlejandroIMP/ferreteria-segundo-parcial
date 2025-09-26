package com.ferreteriagd.sistemaFerreteria.model;

public class Rol {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean puedeVender;
    private boolean puedeGestionarInventario;
    private boolean puedeGestionarUsuarios;
    private boolean puedeGenerarReportes;
    private boolean puedeGestionarClientes;
    private boolean puedeGestionarProveedores;
    private boolean activo;

    // Constructor vacío
    public Rol() {
        this.activo = true;
    }

    // Constructor con parámetros principales
    public Rol(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Constructor para roles predefinidos
    public Rol(String nombre, String descripcion, boolean puedeVender, boolean puedeGestionarInventario,
               boolean puedeGestionarUsuarios, boolean puedeGenerarReportes, boolean puedeGestionarClientes,
               boolean puedeGestionarProveedores) {
        this(nombre, descripcion);
        this.puedeVender = puedeVender;
        this.puedeGestionarInventario = puedeGestionarInventario;
        this.puedeGestionarUsuarios = puedeGestionarUsuarios;
        this.puedeGenerarReportes = puedeGenerarReportes;
        this.puedeGestionarClientes = puedeGestionarClientes;
        this.puedeGestionarProveedores = puedeGestionarProveedores;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isPuedeVender() {
        return puedeVender;
    }

    public void setPuedeVender(boolean puedeVender) {
        this.puedeVender = puedeVender;
    }

    public boolean isPuedeGestionarInventario() {
        return puedeGestionarInventario;
    }

    public void setPuedeGestionarInventario(boolean puedeGestionarInventario) {
        this.puedeGestionarInventario = puedeGestionarInventario;
    }

    public boolean isPuedeGestionarUsuarios() {
        return puedeGestionarUsuarios;
    }

    public void setPuedeGestionarUsuarios(boolean puedeGestionarUsuarios) {
        this.puedeGestionarUsuarios = puedeGestionarUsuarios;
    }

    public boolean isPuedeGenerarReportes() {
        return puedeGenerarReportes;
    }

    public void setPuedeGenerarReportes(boolean puedeGenerarReportes) {
        this.puedeGenerarReportes = puedeGenerarReportes;
    }

    public boolean isPuedeGestionarClientes() {
        return puedeGestionarClientes;
    }

    public void setPuedeGestionarClientes(boolean puedeGestionarClientes) {
        this.puedeGestionarClientes = puedeGestionarClientes;
    }

    public boolean isPuedeGestionarProveedores() {
        return puedeGestionarProveedores;
    }

    public void setPuedeGestionarProveedores(boolean puedeGestionarProveedores) {
        this.puedeGestionarProveedores = puedeGestionarProveedores;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Métodos factory para crear roles predefinidos
    public static Rol createAdministrador() {
        return new Rol("ADMINISTRADOR",
                      "Acceso completo al sistema - puede realizar todas las operaciones",
                      true,  // puedeVender
                      true,  // puedeGestionarInventario
                      true,  // puedeGestionarUsuarios
                      true,  // puedeGenerarReportes
                      true,  // puedeGestionarClientes
                      true); // puedeGestionarProveedores
    }

    public static Rol createVendedor() {
        return new Rol("VENDEDOR",
                      "Especializado en ventas y atención al cliente",
                      true,  // puedeVender
                      false, // puedeGestionarInventario
                      false, // puedeGestionarUsuarios
                      false, // puedeGenerarReportes
                      true,  // puedeGestionarClientes
                      false); // puedeGestionarProveedores
    }

    public static Rol createInventario() {
        return new Rol("INVENTARIO",
                      "Especializado en gestión de productos y proveedores",
                      false, // puedeVender
                      true,  // puedeGestionarInventario
                      false, // puedeGestionarUsuarios
                      true,  // puedeGenerarReportes
                      false, // puedeGestionarClientes
                      true); // puedeGestionarProveedores
    }

    public static Rol createCajero() {
        return new Rol("CAJERO",
                      "Especializado únicamente en realizar ventas",
                      true,  // puedeVender
                      false, // puedeGestionarInventario
                      false, // puedeGestionarUsuarios
                      false, // puedeGenerarReportes
                      false, // puedeGestionarClientes
                      false); // puedeGestionarProveedores
    }

    public static Rol createSupervisor() {
        return new Rol("SUPERVISOR",
                      "Puede supervisar ventas y generar reportes",
                      true,  // puedeVender
                      false, // puedeGestionarInventario
                      false, // puedeGestionarUsuarios
                      true,  // puedeGenerarReportes
                      true,  // puedeGestionarClientes
                      false); // puedeGestionarProveedores
    }

    // Métodos de utilidad
    public boolean tieneAlgunPermiso() {
        return puedeVender || puedeGestionarInventario || puedeGestionarUsuarios ||
               puedeGenerarReportes || puedeGestionarClientes || puedeGestionarProveedores;
    }

    public boolean esSuperUsuario() {
        return puedeVender && puedeGestionarInventario && puedeGestionarUsuarios &&
               puedeGenerarReportes && puedeGestionarClientes && puedeGestionarProveedores;
    }

    public String getResumenPermisos() {
        StringBuilder permisos = new StringBuilder();
        if (puedeVender) permisos.append("Ventas, ");
        if (puedeGestionarInventario) permisos.append("Inventario, ");
        if (puedeGestionarUsuarios) permisos.append("Usuarios, ");
        if (puedeGenerarReportes) permisos.append("Reportes, ");
        if (puedeGestionarClientes) permisos.append("Clientes, ");
        if (puedeGestionarProveedores) permisos.append("Proveedores, ");

        if (permisos.length() > 0) {
            permisos.setLength(permisos.length() - 2); // Remover la última coma y espacio
            return permisos.toString();
        }
        return "Sin permisos";
    }

    public int contarPermisos() {
        int count = 0;
        if (puedeVender) count++;
        if (puedeGestionarInventario) count++;
        if (puedeGestionarUsuarios) count++;
        if (puedeGenerarReportes) count++;
        if (puedeGestionarClientes) count++;
        if (puedeGestionarProveedores) count++;
        return count;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos=" + contarPermisos() +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Rol rol = (Rol) obj;
        return id != null && id.equals(rol.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
