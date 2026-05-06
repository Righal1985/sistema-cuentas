package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // No olvides importar List

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Aquí solo declaramos el método, Spring lo implementa por nosotros
    List<Producto> findByStockLessThan(Integer limite);
}
