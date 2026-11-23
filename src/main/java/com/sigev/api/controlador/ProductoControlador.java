package com.sigev.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sigev.api.dto.ProductoResumen;
import com.sigev.api.dto.ProductoSolicitud;
import com.sigev.api.servicios.ProductoServicio;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    @GetMapping
    public List<ProductoResumen> listar(@RequestParam(name = "q", required = false) String texto) {
        return productoServicio.listar(texto);
    }

    @GetMapping("/reposicion")
    public List<ProductoResumen> listarReposicion() {
        return productoServicio.listarReposicion();
    }

    @GetMapping("/{id}")
    public ProductoResumen obtener(@PathVariable("id") Long id) {
        return productoServicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResumen crear(@RequestBody ProductoSolicitud solicitud) {
        return productoServicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public ProductoResumen actualizar(@PathVariable("id") Long id, @RequestBody ProductoSolicitud solicitud) {
        return productoServicio.actualizar(id, solicitud);
    }

    @PatchMapping("/{id}/estado")
    public ProductoResumen cambiarEstado(@PathVariable("id") Long id, @RequestParam("activo") boolean activo) {
        return productoServicio.cambiarEstado(id, activo);
    }
}
