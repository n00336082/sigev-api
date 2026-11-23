package com.sigev.api.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.dto.PedidoSolicitud;
import com.sigev.api.modelos.Pedido;
import com.sigev.api.modelos.Producto;
import com.sigev.api.repositorio.PedidoRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;

@Service
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final ProductoRepositorio productoRepositorio;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, ProductoRepositorio productoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    public List<Pedido> listar() {
        return pedidoRepositorio.findAllByOrderByFechaEmisionDescIdDesc();
    }

    public Pedido crear(PedidoSolicitud solicitud) {
        if (solicitud.getProveedor() == null || solicitud.getProveedor().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proveedor es obligatorio");
        }
        if (solicitud.getProductoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un producto");
        }
        if (solicitud.getCantidadSolicitada() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad solicitada debe ser mayor a cero");
        }
        Producto producto = productoRepositorio.findById(solicitud.getProductoId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe")
        );
        String tipoCredito = "PLAZOS".equalsIgnoreCase(solicitud.getTipoCredito()) ? "PLAZOS" : "CONTADO";
        String usuario = solicitud.getUsuarioResponsable() == null || solicitud.getUsuarioResponsable().isBlank()
                ? "Christopher Pérez" : solicitud.getUsuarioResponsable().trim();
        String observacion = solicitud.getObservacion() == null ? "" : solicitud.getObservacion().trim();

        Pedido nuevo = new Pedido(
                null,
                solicitud.getProveedor().trim(),
                producto,
                solicitud.getCantidadSolicitada(),
                0,
                solicitud.getPrecioProveedor(),
                tipoCredito,
                LocalDate.now(),
                "PENDIENTE",
                observacion,
                usuario
        );
        return pedidoRepositorio.save(nuevo);
    }

    @Transactional
    public Pedido registrarRecepcion(Long id, int cantidadRecibida) {
        Pedido pedido = pedidoRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El pedido no existe")
        );
        if (cantidadRecibida <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad recibida debe ser mayor a cero");
        }
        int totalRecibido = pedido.getCantidadRecibida() + cantidadRecibida;
        if (totalRecibido > pedido.getCantidadSolicitada()) {
            totalRecibido = pedido.getCantidadSolicitada();
        }
        pedido.setCantidadRecibida(totalRecibido);
        if (totalRecibido >= pedido.getCantidadSolicitada()) {
            pedido.setEstado("COMPLETADO");
        } else {
            pedido.setEstado("PARCIAL");
        }
        return pedidoRepositorio.save(pedido);
    }
}
