package com.sigev.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sigev.api.dto.VentaSolicitud;
import com.sigev.api.modelos.DetalleVenta;
import com.sigev.api.modelos.Venta;
import com.sigev.api.servicios.VentaServicio;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaControlador {

    private final VentaServicio ventaServicio;

    public VentaControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaServicio.listar();
    }

    @GetMapping("/{id}/detalles")
    public List<DetalleVenta> listarDetalles(@PathVariable("id") Long id) {
        return ventaServicio.listarDetalles(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrarVenta(@RequestBody VentaSolicitud solicitud) {
        return ventaServicio.registrarVenta(solicitud);
    }

    @PostMapping("/{id}/devolucion")
    public Venta registrarDevolucion(
            @PathVariable("id") Long id,
            @RequestParam(name = "usuario", required = false) String usuario) {
        return ventaServicio.registrarDevolucion(id, usuario);
    }
}
