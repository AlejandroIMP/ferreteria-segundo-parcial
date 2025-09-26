package com.ferreteriagd.sistemaFerreteria.model;

public enum Estado {
    // Estados para Ventas
    PENDIENTE("Pendiente", "La venta está en proceso"),
    COMPLETADA("Completada", "La venta se ha completado exitosamente"),
    CANCELADA("Cancelada", "La venta ha sido cancelada"),

    // Estados para Productos/Inventario
    DISPONIBLE("Disponible", "Producto disponible para venta"),
    AGOTADO("Agotado", "Producto sin stock"),
    DESCONTINUADO("Descontinuado", "Producto ya no se maneja"),

    // Estados para Usuarios
    ACTIVO("Activo", "Usuario activo en el sistema"),
    INACTIVO("Inactivo", "Usuario desactivado temporalmente"),
    BLOQUEADO("Bloqueado", "Usuario bloqueado por seguridad"),

    // Estados generales
    EN_PROCESO("En Proceso", "Operación en curso"),
    FINALIZADO("Finalizado", "Operación completada"),
    ERROR("Error", "Operación con errores");

    private final String nombre;
    private final String descripcion;

    // Constructor
    Estado(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Métodos de utilidad
    public boolean esEstadoVenta() {
        return this == PENDIENTE || this == COMPLETADA || this == CANCELADA;
    }

    public boolean esEstadoProducto() {
        return this == DISPONIBLE || this == AGOTADO || this == DESCONTINUADO;
    }

    public boolean esEstadoUsuario() {
        return this == ACTIVO || this == INACTIVO || this == BLOQUEADO;
    }

    public boolean esEstadoFinal() {
        return this == COMPLETADA || this == CANCELADA || this == FINALIZADO || this == ERROR;
    }

    public boolean puedeTransicionarA(Estado nuevoEstado) {
        switch (this) {
            case PENDIENTE:
                return nuevoEstado == COMPLETADA || nuevoEstado == CANCELADA;
            case COMPLETADA:
                return false; // Las ventas completadas no pueden cambiar
            case CANCELADA:
                return false; // Las ventas canceladas no pueden cambiar
            case DISPONIBLE:
                return nuevoEstado == AGOTADO || nuevoEstado == DESCONTINUADO;
            case AGOTADO:
                return nuevoEstado == DISPONIBLE || nuevoEstado == DESCONTINUADO;
            case ACTIVO:
                return nuevoEstado == INACTIVO || nuevoEstado == BLOQUEADO;
            case INACTIVO:
                return nuevoEstado == ACTIVO || nuevoEstado == BLOQUEADO;
            case BLOQUEADO:
                return nuevoEstado == ACTIVO;
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}
