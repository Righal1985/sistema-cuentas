package com.mibanco.sistema_cuentas.repository;

import com.mibanco.sistema_cuentas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT SUM(v.total) FROM Venta v")
    Double sumarTotalVentas();

    @Query("SELECT COUNT(v) FROM Venta v")
    Long contarTotalVentas();
}