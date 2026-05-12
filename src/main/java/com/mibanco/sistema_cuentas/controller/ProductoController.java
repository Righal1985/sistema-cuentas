package com.mibanco.sistema_cuentas.controller;
import com.mibanco.sistema_cuentas.model.Cuenta;
import com.mibanco.sistema_cuentas.model.Producto;
import com.mibanco.sistema_cuentas.model.TransferenciaDTO;
import com.mibanco.sistema_cuentas.model.Venta;
import com.mibanco.sistema_cuentas.repository.ProductoRepository;
import com.mibanco.sistema_cuentas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
public class ProductoController {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private VentaRepository ventaRepository;

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
    // BUSCADOR POR NOMBRE (El que acabamos de hacer)
    @GetMapping("/productos/buscar/nombre")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // BUSCADOR POR CATEGORÍA (Versión mejorada)
    @GetMapping("/productos/buscar/categoria")
    public List<Producto> buscarPorCategoria(@RequestParam String cat) {
        // Es mejor usar el repository que los streams para esto también
        return productoRepository.findAll().stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(cat))
                .toList();
    }

    @PostMapping("/productos/{id}/vender")
    public ResponseEntity<?> venderProducto(@PathVariable Long id, @RequestBody Integer cantidad) {
        // 1. Buscamos el producto
        Producto producto = productoRepository.findById(id).orElse(null);

        // Error: Si no existe el producto mandamos un 404
        if (producto == null) {
            return ResponseEntity.status(404).body("Error: Producto no encontrado.");
        }

        // Error: Si no hay stock mandamos un 400
        if (producto.getStock() < cantidad) {
            return ResponseEntity.badRequest().body("Stock insuficiente. Solo quedan " + producto.getStock());
        }

        // 3. Calculamos el total
        Double totalVenta = producto.getPrecio() * cantidad;

        // 4. Actualizamos stock
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        // 5. Guardamos la venta
        Venta nuevaVenta = new Venta(
                producto.getNombre(),
                cantidad,
                totalVenta,
                LocalDateTime.now()
        );
        ventaRepository.save(nuevaVenta);

        if (producto.getStock() < 5) {
            // Si el stock es bajo, enviamos una respuesta personalizada
            return ResponseEntity.ok("VENTA EXITOSA. ¡ALERTA! Stock crítico para " +
                    producto.getNombre() + ": solo quedan " + producto.getStock());
        }

        // 6. DEVOLVEMOS LA VENTA COMPLETA (Spring la convierte automáticamente a JSON)
        return ResponseEntity.ok(nuevaVenta);
    }
    @GetMapping("/productos/reporte-stock")
    public List<Producto> obtenerReporteBajoStock() {
        // Usamos el método que acabamos de definir en el repositorio
        return productoRepository.findByStockLessThan(5);
    }
    @PutMapping("/productos/descuento")
    public String aplicarDescuento(@RequestParam String cat, @RequestParam Double porc) {
        // Guardamos cuántos productos se actualizaron
        int filasActualizadas = productoRepository.aplicarDescuentoPorCategoria(cat, porc);

        if (filasActualizadas == 0) {
            return "Advertencia: No se encontraron productos en la categoría '" + cat + "'. Revisa las mayúsculas.";
        }

        return "¡Éxito! Se actualizó el precio de " + filasActualizadas + " productos de la categoría " + cat;
    }
    @GetMapping("/productos/ventas")
    public List<Venta> obtenerVentas() {
        return ventaRepository.findAll();
    }
    @GetMapping("/productos/reporte")
    public String generarReporte() {
        Double granTotal = ventaRepository.sumarTotalVentas();
        Long cantidadVentas = ventaRepository.contarTotalVentas();

        if (granTotal == null) granTotal = 0.0;

        return "--- REPORTE DE GESTIÓN ---\n" +
                "Total de Ventas Realizadas: " + cantidadVentas + "\n" +
                "Recaudación Total: $" + granTotal;
    }
    @DeleteMapping("/ventas/{id}/anular")
    public ResponseEntity<String> anularVenta(@PathVariable Long id) {
        // 1. Buscar la venta
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            return ResponseEntity.status(404).body("Error: La venta con ID " + id + " no existe.");
        }

        // 2. Buscar el producto por nombre (para devolver el stock)
        // Usamos el nombre que guardamos en la venta
        Producto producto = productoRepository.findAll().stream()
                .filter(p -> p.getNombre().equals(venta.getProductoNombre()))
                .findFirst()
                .orElse(null);

        if (producto != null) {
            // Restauramos el stock
            producto.setStock(producto.getStock() + venta.getCantidad());
            productoRepository.save(producto);
        }

        // 3. Eliminar la venta de la base de datos
        ventaRepository.delete(venta);

        return ResponseEntity.ok("Venta ID " + id + " anulada exitosamente. Stock restaurado.");
    }
    @GetMapping("/ventas")
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }



}
