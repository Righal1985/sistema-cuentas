package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param; // Opcional pero recomendado

import java.util.List; // No olvides importar List

@Repository

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Aquí solo declaramos el método, Spring lo implementa por nosotros
    List<Producto> findByStockLessThan(Integer limite);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Producto p SET p.precio = p.precio * (1.0 - (:porcentaje / 100.0)) WHERE p.categoria = :categoria")
    int aplicarDescuentoPorCategoria(@Param("categoria") String categoria, @Param("porcentaje") Double porcentaje);
}


