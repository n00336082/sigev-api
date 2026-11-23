package com.sigev.api.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sigev.api.modelos.Lote;

public interface LoteRepositorio extends JpaRepository<Lote, Long> {

    Optional<Lote> findByCodigoLoteIgnoreCase(String codigoLote);

    List<Lote> findByProductoIdOrderByFechaVencimientoAsc(Long productoId);

    @Query("SELECT l FROM Lote l WHERE l.producto.id = :productoId AND l.estado = 'ACTIVO' AND l.cantidadDisponible > 0 AND l.fechaVencimiento >= :hoy ORDER BY l.fechaVencimiento ASC, l.id ASC")
    List<Lote> buscarLotesVigentesFefo(@Param("productoId") Long productoId, @Param("hoy") LocalDate hoy);

    @Query("SELECT l FROM Lote l WHERE l.estado = 'ACTIVO' AND l.cantidadDisponible > 0 AND l.fechaVencimiento < :hoy ORDER BY l.fechaVencimiento ASC")
    List<Lote> buscarLotesVencidos(@Param("hoy") LocalDate hoy);

    @Query("SELECT l FROM Lote l WHERE l.estado = 'ACTIVO' AND l.cantidadDisponible > 0 AND l.fechaVencimiento >= :hoy AND l.fechaVencimiento <= :limite ORDER BY l.fechaVencimiento ASC")
    List<Lote> buscarLotesPorVencer(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    @Query("SELECT COALESCE(SUM(l.cantidadDisponible), 0) FROM Lote l WHERE l.producto.id = :productoId AND l.estado = 'ACTIVO' AND l.fechaVencimiento >= :hoy")
    int obtenerStockVigente(@Param("productoId") Long productoId, @Param("hoy") LocalDate hoy);
}
