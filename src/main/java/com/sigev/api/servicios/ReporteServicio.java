package com.sigev.api.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sigev.api.dto.ResumenReporte;
import com.sigev.api.modelos.Lote;
import com.sigev.api.modelos.MovimientoKardex;
import com.sigev.api.modelos.Producto;
import com.sigev.api.modelos.Venta;
import com.sigev.api.repositorio.LoteRepositorio;
import com.sigev.api.repositorio.MovimientoKardexRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;
import com.sigev.api.repositorio.VentaRepositorio;

@Service
public class ReporteServicio {

    private final ProductoRepositorio productoRepositorio;
    private final LoteRepositorio loteRepositorio;
    private final VentaRepositorio ventaRepositorio;
    private final MovimientoKardexRepositorio kardexRepositorio;

    public ReporteServicio(
            ProductoRepositorio productoRepositorio,
            LoteRepositorio loteRepositorio,
            VentaRepositorio ventaRepositorio,
            MovimientoKardexRepositorio kardexRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.loteRepositorio = loteRepositorio;
        this.ventaRepositorio = ventaRepositorio;
        this.kardexRepositorio = kardexRepositorio;
    }

    public ResumenReporte obtenerResumen() {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteAlerta = hoy.plusDays(15);

        double totalVentas = 0;
        long cantidadVentas = 0;
        for (Venta v : ventaRepositorio.findAll()) {
            if ("COMPLETADA".equals(v.getEstado())) {
                totalVentas += v.getTotal();
                cantidadVentas++;
            }
        }

        double valorInventario = 0;
        int unidadesDisponibles = 0;
        int unidadesVencidas = 0;
        double costoExpuestoVencidos = 0;
        long lotesVencidos = 0;
        long lotesPorVencer = 0;

        for (Lote l : loteRepositorio.findAll()) {
            if ("ACTIVO".equals(l.getEstado()) && l.getCantidadDisponible() > 0) {
                if (l.getFechaVencimiento().isBefore(hoy)) {
                    lotesVencidos++;
                    unidadesVencidas += l.getCantidadDisponible();
                    costoExpuestoVencidos += l.getCantidadDisponible() * l.getCostoUnitario();
                } else {
                    unidadesDisponibles += l.getCantidadDisponible();
                    valorInventario += l.getCantidadDisponible() * l.getCostoUnitario();
                    if (!l.getFechaVencimiento().isAfter(limiteAlerta)) {
                        lotesPorVencer++;
                    }
                }
            }
        }

        List<Producto> activos = productoRepositorio.findByActivoTrueOrderByNombreAsc();
        long bajoMinimo = 0;
        for (Producto p : activos) {
            int stock = loteRepositorio.obtenerStockVigente(p.getId(), hoy);
            if (stock <= p.getStockMinimo()) {
                bajoMinimo++;
            }
        }

        double costoMermas = 0;
        for (MovimientoKardex m : kardexRepositorio.findAll()) {
            if ("BAJA_VENCIMIENTO".equals(m.getTipo()) || "BAJA_DANO".equals(m.getTipo())) {
                costoMermas += m.getCostoTotal();
            }
        }

        ResumenReporte r = new ResumenReporte();
        r.setTotalVentas(redondear(totalVentas));
        r.setCantidadVentas(cantidadVentas);
        r.setValorInventario(redondear(valorInventario));
        r.setProductosActivos(activos.size());
        r.setUnidadesDisponibles(unidadesDisponibles);
        r.setProductosBajoMinimo(bajoMinimo);
        r.setLotesVencidos(lotesVencidos);
        r.setUnidadesVencidas(unidadesVencidas);
        r.setCostoExpuestoVencidos(redondear(costoExpuestoVencidos));
        r.setCostoTotalMermas(redondear(costoMermas));
        r.setLotesPorVencer(lotesPorVencer);
        return r;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
