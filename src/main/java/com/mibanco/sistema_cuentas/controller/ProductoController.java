package com.mibanco.sistema_cuentas.controller;
import com.mibanco.sistema_cuentas.model.Cuenta;
import com.mibanco.sistema_cuentas.model.Producto;
import com.mibanco.sistema_cuentas.model.TransferenciaDTO;
import com.mibanco.sistema_cuentas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ProductoController {
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/productos")
    public List<Producto> obtenertodas() {
        return productoRepository.findAll();
    }

    @PostMapping("/productos")
    public Object crear(@RequestBody Producto nuevoProducto) {
        // REGLA DE NEGOCIO: No permitir productos con stock negativo
        if (nuevoProducto.getStock() < 0) {
            return "ERROR: El stock no puede ser negativo.";
        }

        // Guardamos y devolvemos el objeto guardado
        return productoRepository.save(nuevoProducto);
    }


    @PostMapping("/productos/{id}/agregar")
    public Object agregarStock(@PathVariable Long id, @RequestBody Integer cantidad) {
        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null) {
            return "Error: Producto no encontrado.";
        }

        // 1. Validamos si NO alcanza el stock
        if (cantidad <= 0) {
            return "Error: La cantidad a agregar debe ser mayor a cero: " + producto.getStock();
        }

        // 2. SI ALCANZA: El código sigue por acá abajo (fuera del IF anterior)
        producto.setStock(producto.getStock() + cantidad);

        // 3. Guardamos los cambios
        productoRepository.save(producto);

        return "Stock actualizado. Ahora tienes " + producto.getStock() + " unidades de " + producto.getNombre();
    }
    @GetMapping("/productos/buscar")
    public List<Producto> buscarPorCategoria(@RequestParam String cat) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(cat))
                .toList();
    }

    @PostMapping("/productos/{id}/vender")
    public String venderProducto(@PathVariable Long id, @RequestBody Integer cantidad) {
        // 1. Buscamos el producto
        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null) {
            return "Error: Producto no encontrado.";
        }

        // 2. Verificamos si hay suficiente stock
        if (producto.getStock() < cantidad) {
            return "Stock insuficiente. Solo quedan " + producto.getStock() + " unidades.";
        }

        // 3. Calculamos el total (Lógica de Negocio)
        Double totalVenta = producto.getPrecio() * cantidad;

        // 4. Restamos el stock y guardamos
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        // 5. Retornamos un "recibo" al usuario
        return "--- TICKET DE VENTA ---\n" +
                "Producto: " + producto.getNombre() + "\n" +
                "Cantidad: " + cantidad + "\n" +
                "Total a pagar: $" + totalVenta + "\n" +
                "Stock restante: " + producto.getStock();
    }


}
