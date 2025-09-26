package com.ferreteriagd.sistemaFerreteria.model;

import java.math.BigDecimal;

public class DetalleVenta {
    private Long id;
    private Venta venta;
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String observaciones;

    // Constructor vacío
    public DetalleVenta() {
    }

    // Constructor con parámetros principales
    public DetalleVenta(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    // Constructor completo
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this(producto, cantidad, precioUnitario);
        this.venta = venta;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    // Métodos de negocio
    private void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    public boolean stockDisponible() {
        return producto != null && producto.getStock() >= cantidad;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
