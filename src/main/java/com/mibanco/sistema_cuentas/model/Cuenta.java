package com.mibanco.sistema_cuentas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cuentas")
@Data // Esto crea los Getters y Setters automáticamente gracias a Lombok
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titular;
    private Double saldo;
    private String numeroCuenta;
}