package com.ferreteriagd.sistemaFerreteria.model;

import java.time.LocalDateTime;

public class Usuario {
    private Long id;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private boolean activo;

    // Constructor vacío
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    // Constructor con parámetros principales
    public Usuario(String username, String password, String nombre, String apellido, String email, Rol rol) {
        this();
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                '}';
    }

}
