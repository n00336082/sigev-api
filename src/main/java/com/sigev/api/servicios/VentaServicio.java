package com.sigev.api.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.dto.ItemVentaSolicitud;
import com.sigev.api.dto.VentaSolicitud;
import com.sigev.api.modelos.DetalleVenta;
import com.sigev.api.modelos.Lote;
import com.sigev.api.modelos.MovimientoKardex;
import com.sigev.api.modelos.Producto;
import com.sigev.api.modelos.Venta;
import com.sigev.api.repositorio.DetalleVentaRepositorio;
import com.sigev.api.repositorio.LoteRepositorio;
import com.sigev.api.repositorio.MovimientoKardexRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;
import com.sigev.api.repositorio.VentaRepositorio;

@Service
public class VentaServicio {

    private final VentaRepositorio ventaRepositorio;
    private final DetalleVentaRepositorio detalleVentaRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final LoteRepositorio loteRepositorio;
    private final MovimientoKardexRepositorio kardexRepositorio;

    public VentaServicio(
            VentaRepositorio ventaRepositorio,
            DetalleVentaRepositorio detalleVentaRepositorio,
            ProductoRepositorio productoRepositorio,
            LoteRepositorio loteRepositorio,
            MovimientoKardexRepositorio kardexRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
        this.detalleVentaRepositorio = detalleVentaRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.loteRepositorio = loteRepositorio;
        this.kardexRepositorio = kardexRepositorio;
    }

    public List<Venta> listar() {
        return ventaRepositorio.findAllByOrderByFechaHoraDesc();
    }

    public List<DetalleVenta> listarDetalles(Long ventaId) {
        return detalleVentaRepositorio.findByVentaId(ventaId);
    }

    @Transactional
    public Venta registrarVenta(VentaSolicitud solicitud) {
        if (solicitud.getItems() == null || solicitud.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe agregar al menos un producto a la venta");
        }
        LocalDate hoy = LocalDate.now();
        double total = 0;

        for (ItemVentaSolicitud item : solicitud.getItems()) {
            if (item.getProductoId() == null || item.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida en los productos de la venta");
            }
            Producto producto = productoRepositorio.findById(item.getProductoId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado")
            );
            int stockVigente = loteRepositorio.obtenerStockVigente(producto.getId(), hoy);
            if (stockVigente < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock vigente insuficiente para " + producto.getNombre() + " (disponible: " + stockVigente + ")");
            }
            total += producto.getPrecioVenta() * item.getCantidad();
        }

        total = redondear(total);
        double subtotal = redondear(total / 1.18);
        double igv = redondear(total - subtotal);

        String metodoPago = solicitud.getMetodoPago() == null || solicitud.getMetodoPago().isBlank()
                ? "EFECTIVO" : solicitud.getMetodoPago().trim().toUpperCase();
        double montoRecibido = solicitud.getMontoRecibido();
        double vuelto = 0;
        if ("EFECTIVO".equals(metodoPago)) {
            if (montoRecibido < total) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto recibido es menor al total a cobrar");
            }
            vuelto = redondear(montoRecibido - total);
        } else {
            montoRecibido = total;
        }

        String cliente = solicitud.getCliente() == null || solicitud.getCliente().isBlank()
                ? "Público en general" : solicitud.getCliente().trim();
        String usuario = solicitud.getUsuarioResponsable() == null || solicitud.getUsuarioResponsable().isBlank()
                ? "Lucía Ramos" : solicitud.getUsuarioResponsable().trim();
        String comprobante = "V-" + (1001 + ventaRepositorio.count());

        Venta venta = new Venta(
                null,
                comprobante,
                LocalDateTime.now(),
                cliente,
                metodoPago,
                subtotal,
                igv,
                total,
                montoRecibido,
                vuelto,
                usuario,
                "COMPLETADA"
        );
        Venta guardada = ventaRepositorio.save(venta);

        for (ItemVentaSolicitud item : solicitud.getItems()) {
            Producto producto = productoRepositorio.findById(item.getProductoId()).orElseThrow();
            List<Lote> lotesFefo = loteRepositorio.buscarLotesVigentesFefo(producto.getId(), hoy);
            int porDescontar = item.getCantidad();

            for (Lote lote : lotesFefo) {
                if (porDescontar <= 0) {
                    break;
                }
                int descontado = Math.min(lote.getCantidadDisponible(), porDescontar);
                lote.setCantidadDisponible(lote.getCantidadDisponible() - descontado);
                if (lote.getCantidadDisponible() == 0) {
                    lote.setEstado("AGOTADO");
                }
                loteRepositorio.save(lote);

                double subDetalle = redondear(descontado * producto.getPrecioVenta());
                DetalleVenta detalle = new DetalleVenta(null, guardada, producto, lote, descontado, producto.getPrecioVenta(), subDetalle);
                detalleVentaRepositorio.save(detalle);

                int stockActual = loteRepositorio.obtenerStockVigente(producto.getId(), hoy);
                MovimientoKardex mov = new MovimientoKardex(
                        null,
                        LocalDateTime.now(),
                        "VENTA",
                        producto,
                        lote,
                        -descontado,
                        stockActual,
                        redondear(descontado * lote.getCostoUnitario()),
                        usuario,
                        "Venta " + comprobante + " (FEFO lote " + lote.getCodigoLote() + ")"
                );
                kardexRepositorio.save(mov);

                porDescontar -= descontado;
            }
        }
        return guardada;
    }

    @Transactional
    public Venta registrarDevolucion(Long ventaId, String usuarioResponsable) {
        Venta venta = ventaRepositorio.findById(ventaId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "La venta no existe")
        );
        if ("DEVUELTA".equals(venta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta venta ya fue devuelta");
        }
        String usuario = usuarioResponsable == null || usuarioResponsable.isBlank()
                ? "Lucía Ramos" : usuarioResponsable.trim();
        LocalDate hoy = LocalDate.now();

        List<DetalleVenta> detalles = detalleVentaRepositorio.findByVentaId(ventaId);
        for (DetalleVenta d : detalles) {
            Lote lote = d.getLote();
            if (!"BAJA".equals(lote.getEstado())) {
                lote.setCantidadDisponible(lote.getCantidadDisponible() + d.getCantidad());
                lote.setEstado("ACTIVO");
                loteRepositorio.save(lote);
            }
            int stockActual = loteRepositorio.obtenerStockVigente(d.getProducto().getId(), hoy);
            MovimientoKardex mov = new MovimientoKardex(
                    null,
                    LocalDateTime.now(),
                    "DEVOLUCION",
                    d.getProducto(),
                    lote,
                    d.getCantidad(),
                    stockActual,
                    redondear(d.getCantidad() * lote.getCostoUnitario()),
                    usuario,
                    "Devolución de comprobante " + venta.getNumeroComprobante()
            );
            kardexRepositorio.save(mov);
        }

        venta.setEstado("DEVUELTA");
        return ventaRepositorio.save(venta);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
