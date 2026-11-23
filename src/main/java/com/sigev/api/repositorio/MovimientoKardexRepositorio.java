package com.sigev.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sigev.api.modelos.MovimientoKardex;

public interface MovimientoKardexRepositorio extends JpaRepository<MovimientoKardex, Long> {

    List<MovimientoKardex> findAllByOrderByFechaHoraDesc();

    List<MovimientoKardex> findByProductoIdOrderByFechaHoraDesc(Long productoId);

    @Query("SELECT m FROM MovimientoKardex m WHERE m.tipo = :tipo ORDER BY m.fechaHora DESC")
    List<MovimientoKardex> buscarPorTipo(@Param("tipo") String tipo);
}
