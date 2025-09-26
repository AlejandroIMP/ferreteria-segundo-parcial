package com.ferreteriagd.sistemaFerreteria.controller;

import com.ferreteriagd.sistemaFerreteria.model.Producto;
import com.ferreteriagd.sistemaFerreteria.model.Proveedor;
import com.ferreteriagd.sistemaFerreteria.dao.ProductDAO;
import com.ferreteriagd.sistemaFerreteria.dao.ProveedorDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductController {
    private ProductDAO productDAO;
    private ProveedorDAO proveedorDAO;

    public ProductController() {
        this.productDAO = new ProductDAO();
        this.proveedorDAO = new ProveedorDAO();
    }

    // Crear nuevo producto
    public boolean crearProducto(String codigo, String nombre, String descripcion, String categoria,
                                String marca, String modelo, BigDecimal precioCompra, BigDecimal precioVenta,
                                Integer stockMinimo, String unidadMedida, Long proveedorId) {
        try {
            // Validar que el código no exista
            if (buscarPorCodigo(codigo) != null) {
                return false; // Código duplicado
            }

            Producto producto = new Producto(codigo, nombre, descripcion, categoria,
                                           precioCompra, precioVenta, stockMinimo, unidadMedida);
            producto.setMarca(marca);
            producto.setModelo(modelo);

            // Asignar proveedor si se especifica
            if (proveedorId != null) {
                Proveedor proveedor = proveedorDAO.buscarPorId(proveedorId);
                if (proveedor != null) {
                    producto.setProveedor(proveedor);
                }
            }

            return productDAO.crear(producto);
        } catch (Exception e) {
            System.err.println("Error en ProductController.crearProducto: " + e.getMessage());
            return false;
        }
    }

    // Buscar producto por ID
    public Producto buscarPorId(Long id) {
        return productDAO.buscarPorId(id);
    }

    // Buscar producto por código
    public Producto buscarPorCodigo(String codigo) {
        return productDAO.buscarPorCodigo(codigo);
    }

    // Buscar productos por nombre (búsqueda parcial)
    public List<Producto> buscarPorNombre(String nombre) {
        return productDAO.buscarPorNombre(nombre);
    }

    // Buscar productos por categoría
    public List<Producto> buscarPorCategoria(String categoria) {
        return productDAO.obtenerTodos().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    // Obtener todos los productos activos
    public List<Producto> obtenerTodosLosProductos() {
        return productDAO.obtenerTodos();
    }

    // Actualizar producto
    public boolean actualizarProducto(Long id, String nombre, String descripcion, String categoria,
                                    String marca, String modelo, BigDecimal precioCompra,
                                    BigDecimal precioVenta, Integer stockMinimo, String unidadMedida,
                                    Long proveedorId) {
        Producto producto = productDAO.buscarPorId(id);
        if (producto != null) {
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setCategoria(categoria);
            producto.setMarca(marca);
            producto.setModelo(modelo);
            producto.setPrecioCompra(precioCompra);
            producto.setPrecioVenta(precioVenta);
            producto.setStockMinimo(stockMinimo);
            producto.setUnidadMedida(unidadMedida);

            // Actualizar proveedor
            if (proveedorId != null) {
                Proveedor proveedor = proveedorDAO.buscarPorId(proveedorId);
                producto.setProveedor(proveedor);
            }

            return productDAO.actualizar(producto);
        }
        return false;
    }

    // Eliminar producto (soft delete)
    public boolean eliminarProducto(Long id) {
        return productDAO.eliminar(id);
    }

    // Gestión de Stock
    public boolean aumentarStock(Long id, Integer cantidad) {
        Producto producto = productDAO.buscarPorId(id);
        if (producto != null && cantidad > 0) {
            int nuevoStock = producto.getStock() + cantidad;
            return productDAO.actualizarStock(id, nuevoStock);
        }
        return false;
    }

    public boolean reducirStock(Long id, Integer cantidad) {
        Producto producto = productDAO.buscarPorId(id);
        if (producto != null && cantidad > 0 && producto.getStock() >= cantidad) {
            int nuevoStock = producto.getStock() - cantidad;
            return productDAO.actualizarStock(id, nuevoStock);
        }
        return false;
    }

    // Obtener productos con stock bajo
    public List<Producto> obtenerProductosConStockBajo() {
        return productDAO.obtenerProductosConStockBajo();
    }

    // Obtener productos sin stock
    public List<Producto> obtenerProductosSinStock() {
        return productDAO.obtenerTodos().stream()
                .filter(p -> p.getStock() == 0)
                .collect(Collectors.toList());
    }

    // Validar disponibilidad de stock para venta
    public boolean validarStockParaVenta(Long id, Integer cantidad) {
        Producto producto = productDAO.buscarPorId(id);
        return producto != null && producto.getStock() >= cantidad;
    }

    // Obtener categorías disponibles
    public List<String> obtenerCategorias() {
        return productDAO.obtenerTodos().stream()
                .map(Producto::getCategoria)
                .filter(categoria -> categoria != null && !categoria.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Obtener marcas disponibles
    public List<String> obtenerMarcas() {
        return productDAO.obtenerTodos().stream()
                .map(Producto::getMarca)
                .filter(marca -> marca != null && !marca.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Obtener productos por proveedor
    public List<Producto> obtenerProductosPorProveedor(Long proveedorId) {
        return productDAO.obtenerTodos().stream()
                .filter(p -> p.getProveedor() != null &&
                        p.getProveedor().getId().equals(proveedorId))
                .collect(Collectors.toList());
    }

    // Estadísticas
    public int contarProductosActivos() {
        return productDAO.obtenerTodos().size();
    }

    public int contarProductosSinStock() {
        return (int) productDAO.obtenerTodos().stream()
                .filter(p -> p.getStock() == 0)
                .count();
    }

    public BigDecimal calcularValorTotalInventario() {
        return productDAO.calcularValorInventario();
    }
}
