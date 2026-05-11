package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}