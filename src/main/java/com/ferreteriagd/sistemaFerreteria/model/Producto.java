package com.ferreteriagd.sistemaFerreteria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Producto {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String marca;
    private String modelo;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private String unidadMedida;
    private Proveedor proveedor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private boolean activo;

    // Constructor vacío
    public Producto() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.activo = true;
        this.stock = 0;
    }

    // Constructor con parámetros principales
    public Producto(String codigo, String nombre, String descripcion, String categoria,
                   BigDecimal precioCompra, BigDecimal precioVenta, Integer stockMinimo, String unidadMedida) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Métodos de negocio
    public boolean stockBajo() {
        return stock <= stockMinimo;
    }

    public void incrementarStock(Integer cantidad) {
        this.stock += cantidad;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean decrementarStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
            this.fechaActualizacion = LocalDateTime.now();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", activo=" + activo +
                '}';
    }
}
