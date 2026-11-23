package com.sigev.api.consola;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sigev.api.modelos.Lote;
import com.sigev.api.modelos.MovimientoKardex;
import com.sigev.api.modelos.Pedido;
import com.sigev.api.modelos.Producto;
import com.sigev.api.modelos.Ubicacion;
import com.sigev.api.modelos.Usuario;
import com.sigev.api.repositorio.LoteRepositorio;
import com.sigev.api.repositorio.MovimientoKardexRepositorio;
import com.sigev.api.repositorio.PedidoRepositorio;
import com.sigev.api.repositorio.ProductoRepositorio;
import com.sigev.api.repositorio.UbicacionRepositorio;
import com.sigev.api.repositorio.UsuarioRepositorio;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final LoteRepositorio loteRepositorio;
    private final MovimientoKardexRepositorio kardexRepositorio;
    private final PedidoRepositorio pedidoRepositorio;

    public DatosIniciales(
            UsuarioRepositorio usuarioRepositorio,
            UbicacionRepositorio ubicacionRepositorio,
            ProductoRepositorio productoRepositorio,
            LoteRepositorio loteRepositorio,
            MovimientoKardexRepositorio kardexRepositorio,
            PedidoRepositorio pedidoRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.loteRepositorio = loteRepositorio;
        this.kardexRepositorio = kardexRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepositorio.count() == 0) {
            usuarioRepositorio.save(new Usuario(null, "Alejandro Mora", "gerencia@sigev.pe", "DUENO", true));
            usuarioRepositorio.save(new Usuario(null, "Christopher Pérez", "admin@sigev.pe", "ADMINISTRADOR", true));
            usuarioRepositorio.save(new Usuario(null, "Edgar Quispe", "almacen@sigev.pe", "ALMACENERO", true));
            usuarioRepositorio.save(new Usuario(null, "Lucía Ramos", "caja@sigev.pe", "CAJERO", true));
        }

        if (ubicacionRepositorio.count() == 0) {
            Ubicacion u1 = ubicacionRepositorio.save(new Ubicacion(null, "A-01", "Abarrotes - Estante A", "Almacén principal", true));
            Ubicacion u2 = ubicacionRepositorio.save(new Ubicacion(null, "A-02", "Granos - Estante B", "Almacén principal", true));
            Ubicacion u3 = ubicacionRepositorio.save(new Ubicacion(null, "F-01", "Lácteos - Cámara Fría", "Zona fría", true));
            Ubicacion u4 = ubicacionRepositorio.save(new Ubicacion(null, "B-01", "Bebidas - Pasillo 2", "Reserva", true));

            Producto p1 = productoRepositorio.save(new Producto(
                    null, "CF-001", "Café molido 500 g", "Abarrotes", "Unidad (u.)",
                    24.50, 16.00, 40, "Café molido en bolsa sellada de 500 g.", true, u1));
            Producto p2 = productoRepositorio.save(new Producto(
                    null, "LE-001", "Leche entera 1 L", "Lácteos", "Unidad (u.)",
                    5.50, 3.80, 30, "Leche entera pasteurizada en caja de 1 litro.", true, u3));
            Producto p3 = productoRepositorio.save(new Producto(
                    null, "AR-001", "Arroz extra 1 kg", "Abarrotes", "Kilogramo (kg)",
                    4.80, 3.20, 25, "Arroz superior seleccionado bolsa de 1 kg.", true, u2));
            Producto p4 = productoRepositorio.save(new Producto(
                    null, "AC-001", "Aceite vegetal 1 L", "Abarrotes", "Litro (L)",
                    9.90, 7.10, 20, "Aceite vegetal comestible botella de 1 litro.", true, u1));
            Producto p5 = productoRepositorio.save(new Producto(
                    null, "AZ-001", "Azúcar rubia 1 kg", "Abarrotes", "Kilogramo (kg)",
                    4.20, 2.90, 25, "Azúcar rubia doméstica bolsa de 1 kg.", true, u2));
            Producto p6 = productoRepositorio.save(new Producto(
                    null, "AG-001", "Agua mineral 1 L", "Bebidas", "Unidad (u.)",
                    2.50, 1.40, 35, "Agua mineral sin gas botella de 1 litro.", true, u4));

            LocalDate hoy = LocalDate.now();
            LocalDateTime ahora = LocalDateTime.now();

            Lote l1 = loteRepositorio.save(new Lote(null, "CF-1026", p1, hoy.minusDays(15), hoy.plusMonths(5), 120, 120, 16.00, "ACTIVO"));
            Lote l2 = loteRepositorio.save(new Lote(null, "LE-0926", p2, hoy.minusDays(40), hoy.minusDays(2), 18, 18, 3.80, "ACTIVO"));
            Lote l3 = loteRepositorio.save(new Lote(null, "LE-1026", p2, hoy.minusDays(5), hoy.plusDays(12), 96, 96, 3.80, "ACTIVO"));
            Lote l4 = loteRepositorio.save(new Lote(null, "AR-1026", p3, hoy.minusDays(10), hoy.plusMonths(4), 18, 18, 3.20, "ACTIVO"));
            Lote l5 = loteRepositorio.save(new Lote(null, "AC-1026", p4, hoy.minusDays(12), hoy.plusMonths(6), 72, 72, 7.10, "ACTIVO"));
            Lote l6 = loteRepositorio.save(new Lote(null, "AZ-1026", p5, hoy.minusDays(8), hoy.plusMonths(5), 80, 80, 2.90, "ACTIVO"));
            Lote l7 = loteRepositorio.save(new Lote(null, "AG-1026", p6, hoy.minusDays(6), hoy.plusMonths(8), 144, 144, 1.40, "ACTIVO"));

            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(15), "ENTRADA", p1, l1, 120, 120, 120 * 16.00, "Edgar Quispe", "Ingreso inicial de lote CF-1026"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(40), "ENTRADA", p2, l2, 18, 18, 18 * 3.80, "Edgar Quispe", "Ingreso inicial de lote LE-0926"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(5), "ENTRADA", p2, l3, 96, 96, 96 * 3.80, "Edgar Quispe", "Ingreso de lote LE-1026"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(10), "ENTRADA", p3, l4, 18, 18, 18 * 3.20, "Edgar Quispe", "Ingreso inicial de lote AR-1026"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(12), "ENTRADA", p4, l5, 72, 72, 72 * 7.10, "Edgar Quispe", "Ingreso inicial de lote AC-1026"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(8), "ENTRADA", p5, l6, 80, 80, 80 * 2.90, "Edgar Quispe", "Ingreso inicial de lote AZ-1026"));
            kardexRepositorio.save(new MovimientoKardex(null, ahora.minusDays(6), "ENTRADA", p6, l7, 144, 144, 144 * 1.40, "Edgar Quispe", "Ingreso inicial de lote AG-1026"));

            pedidoRepositorio.save(new Pedido(null, "Distribuidora Central SAC", p3, 50, 0, 3.10, "CONTADO", hoy, "PENDIENTE", "Reposición por stock bajo mínimo", "Christopher Pérez"));
        }
    }
}
