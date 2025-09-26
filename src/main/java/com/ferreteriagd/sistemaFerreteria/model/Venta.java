package com.ferreteriagd.sistemaFerreteria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private Long id;
    private String numeroVenta;
    private Cliente cliente;
    private Usuario usuario;
    private LocalDateTime fechaVenta;
    private List<DetalleVenta> detalles;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal descuento;
    private BigDecimal total;
    private Estado estado;
    private BigDecimal iva;
    private String observaciones;
    private String metodoPago;

    // Constructor vacío
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.impuesto = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.estado = Estado.PENDIENTE;
    }

    // Constructor con parámetros principales
    public Venta(String numeroVenta, Cliente cliente, Usuario usuario) {
        this();
        this.numeroVenta = numeroVenta;
        this.cliente = cliente;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        calcularTotales();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
        calcularTotales();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    // Métodos de negocio
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        detalle.setVenta(this);
        calcularTotales();
    }

    public void removerDetalle(DetalleVenta detalle) {
        this.detalles.remove(detalle);
        calcularTotales();
    }

    public void calcularTotales() {
        this.subtotal = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular impuesto (ejemplo: 10%)
        this.impuesto = subtotal.multiply(new BigDecimal("0.10"));

        // Calcular total
        this.total = subtotal.add(impuesto).subtract(descuento);
    }

    public void completarVenta() {
        this.estado = Estado.COMPLETADA;
        // Aquí se podría actualizar el stock de los productos
        for (DetalleVenta detalle : detalles) {
            detalle.getProducto().decrementarStock(detalle.getCantidad());
        }
    }

    public void cancelarVenta() {
        this.estado = Estado.CANCELADA;
    }

    public int getTotalItems() {
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", numeroVenta='" + numeroVenta + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombreCompleto() : "Sin cliente") +
                ", fechaVenta=" + fechaVenta +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }


}
