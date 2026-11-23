package com.sigev.api.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigev.api.modelos.Venta;

public interface VentaRepositorio extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaHoraDesc();
}
