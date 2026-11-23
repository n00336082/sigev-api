package com.sigev.api.servicios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.dto.ProductoResumen;
import com.sigev.api.dto.ProductoSolicitud;
import com.sigev.api.modelos.Lote;
import com.sigev.api.modelos.Producto;
import com.sigev.api.modelos.Ubicacion;
import com.sigev.api.repositorio.LoteRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;
import com.sigev.api.repositorio.UbicacionRepositorio;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final LoteRepositorio loteRepositorio;

    public ProductoServicio(
            ProductoRepositorio productoRepositorio,
            UbicacionRepositorio ubicacionRepositorio,
            LoteRepositorio loteRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.loteRepositorio = loteRepositorio;
    }

    public List<ProductoResumen> listar(String texto) {
        List<Producto> productos;
        if (texto != null && !texto.isBlank()) {
            productos = productoRepositorio.buscarPorTexto(texto.trim());
        } else {
            productos = productoRepositorio.findAll();
        }
        List<ProductoResumen> resultado = new ArrayList<>();
        for (Producto p : productos) {
            resultado.add(construirResumen(p));
        }
        return resultado;
    }

    public List<ProductoResumen> listarReposicion() {
        List<Producto> activos = productoRepositorio.findByActivoTrueOrderByNombreAsc();
        List<ProductoResumen> resultado = new ArrayList<>();
        for (Producto p : activos) {
            ProductoResumen resumen = construirResumen(p);
            if (resumen.isBajoMinimo()) {
                resultado.add(resumen);
            }
        }
        return resultado;
    }

    public ProductoResumen obtener(Long id) {
        Producto producto = productoRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe")
        );
        return construirResumen(producto);
    }

    public ProductoResumen crear(ProductoSolicitud solicitud) {
        if (solicitud.getCodigo() == null || solicitud.getCodigo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código del producto es obligatorio");
        }
        if (solicitud.getNombre() == null || solicitud.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del producto es obligatorio");
        }
        if (solicitud.getUbicacionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una ubicación");
        }
        String codigo = solicitud.getCodigo().trim().toUpperCase();
        if (productoRepositorio.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código de producto ya existe");
        }
        Ubicacion ubicacion = ubicacionRepositorio.findById(solicitud.getUbicacionId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "La ubicación seleccionada no existe")
        );
        if (!ubicacion.isActiva()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ubicación seleccionada no está activa");
        }
        String categoria = solicitud.getCategoria() == null || solicitud.getCategoria().isBlank()
                ? "Abarrotes" : solicitud.getCategoria().trim();
        String unidad = solicitud.getUnidadMedida() == null || solicitud.getUnidadMedida().isBlank()
                ? "Unidad (u.)" : solicitud.getUnidadMedida().trim();
        String descripcion = solicitud.getDescripcion() == null ? "" : solicitud.getDescripcion().trim();

        Producto nuevo = new Producto(
                null,
                codigo,
                solicitud.getNombre().trim(),
                categoria,
                unidad,
                solicitud.getPrecioVenta(),
                solicitud.getCostoUnitario(),
                solicitud.getStockMinimo(),
                descripcion,
                true,
                ubicacion
        );
        Producto guardado = productoRepositorio.save(nuevo);
        return construirResumen(guardado);
    }

    public ProductoResumen actualizar(Long id, ProductoSolicitud solicitud) {
        Producto actual = productoRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe")
        );
        if (solicitud.getNombre() != null && !solicitud.getNombre().isBlank()) {
            actual.setNombre(solicitud.getNombre().trim());
        }
        if (solicitud.getCategoria() != null && !solicitud.getCategoria().isBlank()) {
            actual.setCategoria(solicitud.getCategoria().trim());
        }
        if (solicitud.getUnidadMedida() != null && !solicitud.getUnidadMedida().isBlank()) {
            actual.setUnidadMedida(solicitud.getUnidadMedida().trim());
        }
        if (solicitud.getPrecioVenta() >= 0) {
            actual.setPrecioVenta(solicitud.getPrecioVenta());
        }
        if (solicitud.getCostoUnitario() >= 0) {
            actual.setCostoUnitario(solicitud.getCostoUnitario());
        }
        if (solicitud.getStockMinimo() >= 0) {
            actual.setStockMinimo(solicitud.getStockMinimo());
        }
        if (solicitud.getDescripcion() != null) {
            actual.setDescripcion(solicitud.getDescripcion().trim());
        }
        if (solicitud.getUbicacionId() != null) {
            Ubicacion ubicacion = ubicacionRepositorio.findById(solicitud.getUbicacionId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La ubicación seleccionada no existe")
            );
            actual.setUbicacion(ubicacion);
        }
        return construirResumen(productoRepositorio.save(actual));
    }

    public ProductoResumen cambiarEstado(Long id, boolean activo) {
        Producto actual = productoRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe")
        );
        actual.setActivo(activo);
        return construirResumen(productoRepositorio.save(actual));
    }

    private ProductoResumen construirResumen(Producto p) {
        LocalDate hoy = LocalDate.now();
        List<Lote> lotes = loteRepositorio.findByProductoIdOrderByFechaVencimientoAsc(p.getId());
        int stockVigente = 0;
        int stockVencido = 0;
        for (Lote l : lotes) {
            if ("ACTIVO".equals(l.getEstado()) && l.getCantidadDisponible() > 0) {
                if (l.getFechaVencimiento().isBefore(hoy)) {
                    stockVencido += l.getCantidadDisponible();
                } else {
                    stockVigente += l.getCantidadDisponible();
                }
            }
        }
        ProductoResumen r = new ProductoResumen();
        r.setId(p.getId());
        r.setCodigo(p.getCodigo());
        r.setNombre(p.getNombre());
        r.setCategoria(p.getCategoria());
        r.setUnidadMedida(p.getUnidadMedida());
        r.setPrecioVenta(p.getPrecioVenta());
        r.setCostoUnitario(p.getCostoUnitario());
        r.setStockMinimo(p.getStockMinimo());
        r.setDescripcion(p.getDescripcion());
        r.setActivo(p.isActivo());
        r.setUbicacionId(p.getUbicacion().getId());
        r.setUbicacionCodigo(p.getUbicacion().getCodigo());
        r.setUbicacionNombre(p.getUbicacion().getNombre());
        r.setStockVigente(stockVigente);
        r.setStockVencido(stockVencido);
        r.setBajoMinimo(stockVigente <= p.getStockMinimo());
        return r;
    }
}
