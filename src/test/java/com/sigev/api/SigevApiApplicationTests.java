package com.sigev.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.dto.EntradaLoteSolicitud;
import com.sigev.api.dto.ItemVentaSolicitud;
import com.sigev.api.dto.ProductoResumen;
import com.sigev.api.dto.ProductoSolicitud;
import com.sigev.api.dto.VentaSolicitud;
import com.sigev.api.modelos.Ubicacion;
import com.sigev.api.modelos.Venta;
import com.sigev.api.repositorio.UbicacionRepositorio;
import com.sigev.api.servicios.InventarioServicio;
import com.sigev.api.servicios.ProductoServicio;
import com.sigev.api.servicios.VentaServicio;

@SpringBootTest
@Transactional
class SigevApiApplicationTests {

    @Autowired
    private UbicacionRepositorio ubicacionRepositorio;

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    private VentaServicio ventaServicio;

    @Test
    void productoNuevoIniciaConStockCeroYRechazaDuplicado() {
        Ubicacion ubicacion = ubicacionRepositorio.findAll().get(0);

        ProductoSolicitud solicitud = new ProductoSolicitud();
        solicitud.setCodigo("PR-TEST-01");
        solicitud.setNombre("Galleta integral 200 g");
        solicitud.setCategoria("Abarrotes");
        solicitud.setUnidadMedida("Unidad (u.)");
        solicitud.setPrecioVenta(3.50);
        solicitud.setCostoUnitario(2.10);
        solicitud.setStockMinimo(10);
        solicitud.setUbicacionId(ubicacion.getId());

        ProductoResumen creado = productoServicio.crear(solicitud);
        assertEquals(0, creado.getStockVigente());
        assertTrue(creado.isBajoMinimo());

        assertThrows(ResponseStatusException.class, () -> productoServicio.crear(solicitud));
    }

    @Test
    void ventaAplicaFefoYBloqueaLotesVencidos() {
        Ubicacion ubicacion = ubicacionRepositorio.findAll().get(0);

        ProductoSolicitud ps = new ProductoSolicitud();
        ps.setCodigo("PR-FEFO-01");
        ps.setNombre("Yogurt natural 1 L");
        ps.setCategoria("Lácteos");
        ps.setUnidadMedida("Unidad (u.)");
        ps.setPrecioVenta(8.00);
        ps.setCostoUnitario(5.00);
        ps.setStockMinimo(5);
        ps.setUbicacionId(ubicacion.getId());
        ProductoResumen producto = productoServicio.crear(ps);

        EntradaLoteSolicitud loteVencido = new EntradaLoteSolicitud();
        loteVencido.setProductoId(producto.getId());
        loteVencido.setCodigoLote("L-VENC-01");
        loteVencido.setFechaVencimiento(LocalDate.now().minusDays(3));
        loteVencido.setCantidad(10);
        inventarioServicio.registrarEntrada(loteVencido);

        EntradaLoteSolicitud lote1 = new EntradaLoteSolicitud();
        lote1.setProductoId(producto.getId());
        lote1.setCodigoLote("L-FEFO-01");
        lote1.setFechaVencimiento(LocalDate.now().plusDays(5));
        lote1.setCantidad(4);
        inventarioServicio.registrarEntrada(lote1);

        EntradaLoteSolicitud lote2 = new EntradaLoteSolicitud();
        lote2.setProductoId(producto.getId());
        lote2.setCodigoLote("L-FEFO-02");
        lote2.setFechaVencimiento(LocalDate.now().plusDays(20));
        lote2.setCantidad(6);
        inventarioServicio.registrarEntrada(lote2);

        ProductoResumen antes = productoServicio.obtener(producto.getId());
        assertEquals(10, antes.getStockVigente());
        assertEquals(10, antes.getStockVencido());

        ItemVentaSolicitud item = new ItemVentaSolicitud();
        item.setProductoId(producto.getId());
        item.setCantidad(6);

        VentaSolicitud ventaSolicitud = new VentaSolicitud();
        ventaSolicitud.setCliente("Cliente prueba");
        ventaSolicitud.setMetodoPago("EFECTIVO");
        ventaSolicitud.setMontoRecibido(50.00);
        ventaSolicitud.setUsuarioResponsable("Lucía Ramos");
        ventaSolicitud.setItems(List.of(item));

        Venta venta = ventaServicio.registrarVenta(ventaSolicitud);
        assertEquals(48.00, venta.getTotal());
        assertEquals(2.00, venta.getVuelto());

        ProductoResumen despues = productoServicio.obtener(producto.getId());
        assertEquals(4, despues.getStockVigente());
        assertEquals(2, ventaServicio.listarDetalles(venta.getId()).size());
    }
}
