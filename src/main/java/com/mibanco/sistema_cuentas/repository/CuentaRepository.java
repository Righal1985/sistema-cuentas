package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    // Aquí ya tenemos GRATIS los métodos: save(), findAll(), delete(), etc.
}

