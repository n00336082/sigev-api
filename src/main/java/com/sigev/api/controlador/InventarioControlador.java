package com.sigev.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sigev.api.dto.BajaLoteSolicitud;
import com.sigev.api.dto.EntradaLoteSolicitud;
import com.sigev.api.dto.LoteResumen;
import com.sigev.api.modelos.MovimientoKardex;
import com.sigev.api.servicios.InventarioServicio;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class InventarioControlador {

    private final InventarioServicio inventarioServicio;

    public InventarioControlador(InventarioServicio inventarioServicio) {
        this.inventarioServicio = inventarioServicio;
    }

    @GetMapping("/lotes")
    public List<LoteResumen> listarLotes(@RequestParam(name = "situacion", required = false) String situacion) {
        return inventarioServicio.listarLotes(situacion);
    }

    @PostMapping("/entradas")
    @ResponseStatus(HttpStatus.CREATED)
    public LoteResumen registrarEntrada(@RequestBody EntradaLoteSolicitud solicitud) {
        return inventarioServicio.registrarEntrada(solicitud);
    }

    @PostMapping("/bajas")
    public LoteResumen registrarBaja(@RequestBody BajaLoteSolicitud solicitud) {
        return inventarioServicio.registrarBaja(solicitud);
    }

    @GetMapping("/kardex")
    public List<MovimientoKardex> listarKardex(
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "productoId", required = false) Long productoId) {
        return inventarioServicio.listarKardex(tipo, productoId);
    }
}
