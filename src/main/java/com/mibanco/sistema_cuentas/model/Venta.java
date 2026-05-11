package com.mibanco.sistema_cuentas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productoNombre;
    private Integer cantidad;
    private Double total;
    private LocalDateTime fecha;

    // 1. Constructor vacío (Obligatorio para JPA)
    public Venta() {}

    // 2. Constructor con parámetros (Para crear ventas fácilmente)
    public Venta(String productoNombre, Integer cantidad, Double total, LocalDateTime fecha) {
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.total = total;
        this.fecha = fecha;
    }

    // 3. Getters y Setters (Las "puertas" para acceder a los datos)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}