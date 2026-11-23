package com.sigev.api.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sigev.api.modelos.Ubicacion;

public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Long> {

    Optional<Ubicacion> findByCodigoIgnoreCase(String codigo);

    List<Ubicacion> findByActivaTrueOrderByCodigoAsc();
}
