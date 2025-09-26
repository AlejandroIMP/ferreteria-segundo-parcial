package com.ferreteriagd.sistemaFerreteria.model;

import java.time.LocalDateTime;

public class Proveedor {
    private Long id;
    private String nombre;
    private String razonSocial;
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private String contacto;
    private String telefonoContacto;
    private LocalDateTime fechaRegistro;
    private boolean activo;

    // Constructor vacío
    public Proveedor() {
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor con parámetros principales
    public Proveedor(String nombre, String razonSocial, String ruc, String telefono, String email, String direccion) {
        this();
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
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

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", ruc='" + ruc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }

}
