package com.sigev.api.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.dto.BajaLoteSolicitud;
import com.sigev.api.dto.EntradaLoteSolicitud;
import com.sigev.api.dto.LoteResumen;
import com.sigev.api.modelos.Lote;
import com.sigev.api.modelos.MovimientoKardex;
import com.sigev.api.modelos.Producto;
import com.sigev.api.repositorio.LoteRepositorio;
import com.sigev.api.repositorio.MovimientoKardexRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;

@Service
public class InventarioServicio {

    private static final int DIAS_ALERTA = 15;

    private final LoteRepositorio loteRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final MovimientoKardexRepositorio kardexRepositorio;

    public InventarioServicio(
            LoteRepositorio loteRepositorio,
            ProductoRepositorio productoRepositorio,
            MovimientoKardexRepositorio kardexRepositorio) {
        this.loteRepositorio = loteRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.kardexRepositorio = kardexRepositorio;
    }

    public List<LoteResumen> listarLotes(String situacion) {
        List<Lote> lotes = loteRepositorio.findAll();
        List<LoteResumen> resultado = new ArrayList<>();
        for (Lote l : lotes) {
            LoteResumen r = construirResumen(l);
            if (situacion == null || situacion.isBlank() || r.getSituacion().equalsIgnoreCase(situacion.trim())) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Transactional
    public LoteResumen registrarEntrada(EntradaLoteSolicitud solicitud) {
        if (solicitud.getProductoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un producto");
        }
        if (solicitud.getCodigoLote() == null || solicitud.getCodigoLote().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código de lote es obligatorio");
        }
        if (solicitud.getFechaVencimiento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento es obligatoria");
        }
        if (solicitud.getCantidad() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a cero");
        }
        String codigoLote = solicitud.getCodigoLote().trim().toUpperCase();
        if (loteRepositorio.findByCodigoLoteIgnoreCase(codigoLote).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un lote con ese código");
        }
        Producto producto = productoRepositorio.findById(solicitud.getProductoId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe")
        );
        double costo = solicitud.getCostoUnitario() > 0 ? solicitud.getCostoUnitario() : producto.getCostoUnitario();
        String usuario = solicitud.getUsuarioResponsable() == null || solicitud.getUsuarioResponsable().isBlank()
                ? "Edgar Quispe" : solicitud.getUsuarioResponsable().trim();
        String observacion = solicitud.getObservacion() == null || solicitud.getObservacion().isBlank()
                ? "Entrada de mercadería - Lote " + codigoLote : solicitud.getObservacion().trim();

        Lote nuevo = new Lote(
                null,
                codigoLote,
                producto,
                LocalDate.now(),
                solicitud.getFechaVencimiento(),
                solicitud.getCantidad(),
                solicitud.getCantidad(),
                costo,
                "ACTIVO"
        );
        Lote guardado = loteRepositorio.save(nuevo);

        int stockActual = loteRepositorio.obtenerStockVigente(producto.getId(), LocalDate.now());
        MovimientoKardex mov = new MovimientoKardex(
                null,
                LocalDateTime.now(),
                "ENTRADA",
                producto,
                guardado,
                solicitud.getCantidad(),
                stockActual,
                solicitud.getCantidad() * costo,
                usuario,
                observacion
        );
        kardexRepositorio.save(mov);
        return construirResumen(guardado);
    }

    @Transactional
    public LoteResumen registrarBaja(BajaLoteSolicitud solicitud) {
        if (solicitud.getLoteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un lote");
        }
        Lote lote = loteRepositorio.findById(solicitud.getLoteId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El lote no existe")
        );
        if (lote.getCantidadDisponible() <= 0 || "BAJA".equals(lote.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lote no tiene unidades disponibles para dar de baja");
        }
        int cantidad = solicitud.getCantidad() > 0 ? solicitud.getCantidad() : lote.getCantidadDisponible();
        if (cantidad > lote.getCantidadDisponible()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad supera el disponible del lote");
        }
        String tipo = "BAJA_DANO".equalsIgnoreCase(solicitud.getTipo()) ? "BAJA_DANO" : "BAJA_VENCIMIENTO";
        String usuario = solicitud.getUsuarioResponsable() == null || solicitud.getUsuarioResponsable().isBlank()
                ? "Edgar Quispe" : solicitud.getUsuarioResponsable().trim();
        String observacion = solicitud.getObservacion() == null || solicitud.getObservacion().isBlank()
                ? "Baja de lote " + lote.getCodigoLote() : solicitud.getObservacion().trim();

        int restante = lote.getCantidadDisponible() - cantidad;
        lote.setCantidadDisponible(restante);
        if (restante == 0) {
            lote.setEstado("BAJA");
        }
        Lote actualizado = loteRepositorio.save(lote);

        int stockActual = loteRepositorio.obtenerStockVigente(lote.getProducto().getId(), LocalDate.now());
        MovimientoKardex mov = new MovimientoKardex(
                null,
                LocalDateTime.now(),
                tipo,
                lote.getProducto(),
                actualizado,
                -cantidad,
                stockActual,
                cantidad * lote.getCostoUnitario(),
                usuario,
                observacion
        );
        kardexRepositorio.save(mov);
        return construirResumen(actualizado);
    }

    public List<MovimientoKardex> listarKardex(String tipo, Long productoId) {
        if (productoId != null) {
            return kardexRepositorio.findByProductoIdOrderByFechaHoraDesc(productoId);
        }
        if (tipo != null && !tipo.isBlank()) {
            return kardexRepositorio.buscarPorTipo(tipo.trim().toUpperCase());
        }
        return kardexRepositorio.findAllByOrderByFechaHoraDesc();
    }

    public LoteResumen construirResumen(Lote l) {
        LocalDate hoy = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento());
        String situacion;
        if ("BAJA".equals(l.getEstado())) {
            situacion = "BAJA";
        } else if (l.getCantidadDisponible() <= 0 || "AGOTADO".equals(l.getEstado())) {
            situacion = "AGOTADO";
        } else if (dias < 0) {
            situacion = "VENCIDO";
        } else if (dias <= DIAS_ALERTA) {
            situacion = "POR_VENCER";
        } else {
            situacion = "VIGENTE";
        }

        LoteResumen r = new LoteResumen();
        r.setId(l.getId());
        r.setCodigoLote(l.getCodigoLote());
        r.setProductoId(l.getProducto().getId());
        r.setProductoCodigo(l.getProducto().getCodigo());
        r.setProductoNombre(l.getProducto().getNombre());
        r.setUbicacionCodigo(l.getProducto().getUbicacion().getCodigo());
        r.setFechaIngreso(l.getFechaIngreso());
        r.setFechaVencimiento(l.getFechaVencimiento());
        r.setCantidadInicial(l.getCantidadInicial());
        r.setCantidadDisponible(l.getCantidadDisponible());
        r.setCostoUnitario(l.getCostoUnitario());
        r.setCostoExpuesto(Math.round(l.getCantidadDisponible() * l.getCostoUnitario() * 100.0) / 100.0);
        r.setDiasRestantes(dias);
        r.setEstado(l.getEstado());
        r.setSituacion(situacion);
        return r;
    }
}
