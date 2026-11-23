package com.sigev.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sigev.api.dto.PedidoSolicitud;
import com.sigev.api.modelos.Pedido;
import com.sigev.api.servicios.PedidoServicio;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoControlador {

    private final PedidoServicio pedidoServicio;

    public PedidoControlador(PedidoServicio pedidoServicio) {
        this.pedidoServicio = pedidoServicio;
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoServicio.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crear(@RequestBody PedidoSolicitud solicitud) {
        return pedidoServicio.crear(solicitud);
    }

    @PatchMapping("/{id}/recepcion")
    public Pedido registrarRecepcion(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad) {
        return pedidoServicio.registrarRecepcion(id, cantidad);
    }
}
