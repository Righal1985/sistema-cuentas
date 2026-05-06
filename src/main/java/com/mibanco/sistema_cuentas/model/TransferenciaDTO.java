package com.mibanco.sistema_cuentas.model;

public class TransferenciaDTO {
    private Long idOrigen;
    private Long idDestino;
    private Double monto;

    // Getters y Setters (Son como los de Android para acceder a los datos)
    public Long getIdOrigen() { return idOrigen; }
    public void setIdOrigen(Long idOrigen) { this.idOrigen = idOrigen; }

    public Long getIdDestino() { return idDestino; }
    public void setIdDestino(Long idDestino) { this.idDestino = idDestino; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}