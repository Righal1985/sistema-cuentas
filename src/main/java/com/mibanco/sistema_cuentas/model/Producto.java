package com.mibanco.sistema_cuentas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Producto")
@Data

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;

    private String nombre;
    private Double precio;
    private Integer stock;
    private String categoria;

}
