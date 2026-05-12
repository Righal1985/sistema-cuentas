package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 1. Buscador por nombre (Parte C: Ignora mayúsculas y busca coincidencias parciales)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // 2. Consulta para ver quiénes están bajo el límite de stock (Para reportes)
    List<Producto> findByStockLessThan(Integer limite);

    // 3. Operación masiva: Aplicar descuento a toda una categoría
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Producto p SET p.precio = p.precio * (1.0 - (:porcentaje / 100.0)) WHERE p.categoria = :categoria")
    int aplicarDescuentoPorCategoria(@Param("categoria") String categoria, @Param("porcentaje") Double porcentaje);
}


