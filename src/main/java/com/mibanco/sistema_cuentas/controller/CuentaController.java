package com.mibanco.sistema_cuentas.controller;

import com.mibanco.sistema_cuentas.model.Cuenta;
import com.mibanco.sistema_cuentas.model.TransferenciaDTO;
import com.mibanco.sistema_cuentas.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CuentaController {

    @Autowired
    private CuentaRepository cuentaRepository; // ESTA LÍNEA ES VITAL

    @GetMapping("/cuentas")
    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll();
    }

    @PostMapping("/cuentas")
    public Object crear(@RequestBody Cuenta nuevaCuenta) {
        // REGLA DE NEGOCIO: El saldo no puede ser menor a 0
        if (nuevaCuenta.getSaldo() < 0) {
            return "ERROR: No se puede crear una cuenta con saldo negativo. ¡Operación cancelada!";
        }

        // Si el saldo es 0 o más, guardamos normalmente
        return cuentaRepository.save(nuevaCuenta);
    }

    @GetMapping("/bienvenida")
    public String saludar() {
        return "Servidor activo. Prueba entrar a /cuentas para ver los datos.";
    }
    @PostMapping("/cuentas/{id}/retirar")
    public Object retirar(@PathVariable Long id, @RequestBody Double monto) {
        Cuenta cuenta = cuentaRepository.findById(id).orElse(null);

        if (cuenta == null) {
            return "Error: Cuenta no encontrada.";
        }

        // 1. Verificamos si tiene suficiente dinero
        if (cuenta.getSaldo() < monto) {
            return "Error: Saldo Insuficiente. Tu saldo actual es: " + cuenta.getSaldo();
        }

        // 2. Si llegamos aquí, es porque SÍ hay plata. Restamos y guardamos.
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        cuentaRepository.save(cuenta);

        return "Retiro exitoso. Nuevo saldo: " + cuenta.getSaldo();
    }
    @PostMapping("/cuentas/transferir")
    public String transferir(@RequestBody TransferenciaDTO datos) {
        // 1. Buscamos ambas cuentas
        Cuenta origen = cuentaRepository.findById(datos.getIdOrigen()).orElse(null);
        Cuenta destino = cuentaRepository.findById(datos.getIdDestino()).orElse(null);

        // 2. Validaciones de seguridad
        if (origen == null || destino == null) {
            return "Error: Una o ambas cuentas no existen.";
        }
        if (origen.getSaldo() < datos.getMonto()) {
            return "Error: Saldo insuficiente en la cuenta de origen.";
        }

        // 3. LA DOBLE JUGADA: Restamos a uno y sumamos al otro
        origen.setSaldo(origen.getSaldo() - datos.getMonto());
        destino.setSaldo(destino.getSaldo() + datos.getMonto());

        // 4. Guardamos ambos cambios
        cuentaRepository.save(origen);
        cuentaRepository.save(destino);

        return "Transferencia exitosa de $" + datos.getMonto() + " desde " + origen.getTitular() + " a " + destino.getTitular();
    }
}