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

import com.sigev.api.modelos.Ubicacion;
import com.sigev.api.servicios.UbicacionServicio;

@RestController
@RequestMapping("/api/ubicaciones")
@CrossOrigin(origins = "http://localhost:4200")
public class UbicacionControlador {

    private final UbicacionServicio ubicacionServicio;

    public UbicacionControlador(UbicacionServicio ubicacionServicio) {
        this.ubicacionServicio = ubicacionServicio;
    }

    @GetMapping
    public List<Ubicacion> listar(@RequestParam(name = "soloActivas", defaultValue = "false") boolean soloActivas) {
        if (soloActivas) {
            return ubicacionServicio.listarActivas();
        }
        return ubicacionServicio.listar();
    }

    @GetMapping("/{id}")
    public Ubicacion obtener(@PathVariable("id") Long id) {
        return ubicacionServicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ubicacion crear(@RequestBody Ubicacion ubicacion) {
        return ubicacionServicio.crear(ubicacion);
    }

    @PutMapping("/{id}")
    public Ubicacion actualizar(@PathVariable("id") Long id, @RequestBody Ubicacion ubicacion) {
        return ubicacionServicio.actualizar(id, ubicacion);
    }

    @PatchMapping("/{id}/estado")
    public Ubicacion cambiarEstado(@PathVariable("id") Long id, @RequestParam("activa") boolean activa) {
        return ubicacionServicio.cambiarEstado(id, activa);
    }
}
