package com.sigev.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigev.api.modelos.DetalleVenta;

public interface DetalleVentaRepositorio extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);
}
