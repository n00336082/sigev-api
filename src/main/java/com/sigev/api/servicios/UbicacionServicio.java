package com.sigev.api.servicios;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.modelos.Ubicacion;
import com.sigev.api.repositorio.UbicacionRepositorio;

@Service
public class UbicacionServicio {

    private final UbicacionRepositorio ubicacionRepositorio;

    public UbicacionServicio(UbicacionRepositorio ubicacionRepositorio) {
        this.ubicacionRepositorio = ubicacionRepositorio;
    }

    public List<Ubicacion> listar() {
        return ubicacionRepositorio.findAll();
    }

    public List<Ubicacion> listarActivas() {
        return ubicacionRepositorio.findByActivaTrueOrderByCodigoAsc();
    }

    public Ubicacion obtener(Long id) {
        return ubicacionRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "La ubicación no existe")
        );
    }

    public Ubicacion crear(Ubicacion datos) {
        if (datos.getCodigo() == null || datos.getCodigo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código de ubicación es obligatorio");
        }
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de ubicación es obligatorio");
        }
        if (datos.getZona() == null || datos.getZona().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La zona es obligatoria");
        }
        String codigo = datos.getCodigo().trim().toUpperCase();
        if (ubicacionRepositorio.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una ubicación con ese código");
        }
        Ubicacion nueva = new Ubicacion(null, codigo, datos.getNombre().trim(), datos.getZona().trim(), true);
        return ubicacionRepositorio.save(nueva);
    }

    public Ubicacion actualizar(Long id, Ubicacion datos) {
        Ubicacion actual = obtener(id);
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            actual.setNombre(datos.getNombre().trim());
        }
        if (datos.getZona() != null && !datos.getZona().isBlank()) {
            actual.setZona(datos.getZona().trim());
        }
        actual.setActiva(datos.isActiva());
        return ubicacionRepositorio.save(actual);
    }

    public Ubicacion cambiarEstado(Long id, boolean activa) {
        Ubicacion actual = obtener(id);
        actual.setActiva(activa);
        return ubicacionRepositorio.save(actual);
    }
}
